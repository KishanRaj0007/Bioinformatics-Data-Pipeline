import os
import pandas as pd
import numpy as np
import xgboost as xgb
import joblib
from fastapi import FastAPI, HTTPException
from pymongo import MongoClient
from sklearn.model_selection import train_test_split
from pydantic import BaseModel
from fastapi.middleware.cors import CORSMiddleware

# --- FastAPI & MongoDB Setup ---
app = FastAPI(title="ML Inference Service")

origins = [
    "http://localhost:3000",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

MONGO_URI = os.getenv("MONGO_URI", "mongodb://mongodb:27017/")
DB_NAME = "bio_platform_db"

client = MongoClient(MONGO_URI)
db = client[DB_NAME]

# --- Pydantic Models for API Request Body ---
class PredictionRequest(BaseModel):
    sample_id: str

# --- Machine Learning Logic ---
MODEL_FILE_PATH = "model.joblib"

def create_feature_matrix():
    """
    Queries MongoDB and creates a feature matrix for training.
    """
    variants_collection = db["variants"]
    variants_cursor = variants_collection.find({}, {"_id": 1, "sampleIds": 1})
    
    # --- Data Simulation ---
    # default to empty list so "in" checks are safe
    all_variants = {doc['_id']: doc.get('sampleIds', []) for doc in variants_cursor}
    
    simulated_data = {
        "HG002": {"chr1:12345:A:T": 1, "chr2:54321:G:C": 0, "disease_status": 0},
        "HG003": {"chr1:12345:A:T": 0, "chr2:54321:G:C": 1, "disease_status": 1},
        "HG004": {"chr1:12345:A:T": 1, "chr2:54321:G:C": 1, "disease_status": 1},
    }
    
    real_sample_id = "HG001"
    simulated_data[real_sample_id] = {
        variant_id: 1 if real_sample_id in samples else 0 
        for variant_id, samples in all_variants.items()
    }
    simulated_data[real_sample_id]["disease_status"] = 0

    # --- Feature Engineering ---
    df = pd.DataFrame.from_dict(simulated_data, orient='index').fillna(0)
    
    X = df.drop("disease_status", axis=1)
    y = df["disease_status"]
    
    return X, y

@app.post("/train")
def train_model():
    """
    Fetches data, trains an XGBoost model, and saves it to a file.
    """
    try:
        X, y = create_feature_matrix()
        
        if len(X) < 2:
            return {"message": "Not enough data to train a model."}

        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

        model = xgb.XGBClassifier(objective="binary:logistic", eval_metric="logloss", use_label_encoder=False)
        model.fit(X_train, y_train)

        # Save model together with feature names to ensure consistent feature ordering at predict time
        joblib.dump({"model": model, "feature_names": X.columns.tolist()}, MODEL_FILE_PATH)

        accuracy = model.score(X_test, y_test)
        return {"message": "Model trained successfully.", "accuracy_on_test_set": accuracy}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Model training failed: {str(e)}")

@app.post("/predict")
def predict(request: PredictionRequest):
    """
    Loads the trained model and makes a prediction for a given sample_id.
    """
    if not os.path.exists(MODEL_FILE_PATH):
        raise HTTPException(status_code=404, detail="Model not found. Please train the model first.")

    try:
        model_data = joblib.load(MODEL_FILE_PATH)
        model = model_data["model"]
        feature_names = model_data.get("feature_names", [])

        # Build feature vector in the same order used for training
        variants_collection = db["variants"]
        sample_variants_cursor = variants_collection.find({"sampleIds": request.sample_id}, {"_id": 1})
        sample_variant_set = {doc['_id'] for doc in sample_variants_cursor}

        feature_vector = [1 if feat in sample_variant_set else 0 for feat in feature_names]

        if len(feature_vector) != len(feature_names) or len(feature_names) == 0:
            raise HTTPException(status_code=500, detail="Feature mismatch between training and prediction. Retrain model with current DB variants.")

        arr = np.array(feature_vector).reshape(1, -1)
        prediction = model.predict(arr)
        probability = model.predict_proba(arr)
        
        # Get the probability for the predicted class (use first row)
        predicted_class_index = int(prediction[0])
        confidence_score = float(probability[0, predicted_class_index])

        return {
            "sample_id": request.sample_id,
            "prediction": "diseased" if predicted_class_index == 1 else "healthy",
            "confidence_score": float(confidence_score)
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Prediction failed: {str(e)}")

# --- Root and DB Test Endpoints ---
@app.get("/")
def read_root():
    return {"message": "ML Inference Service is running."}

@app.get("/test-db")
def test_db_connection():
    try:
        client.admin.command('ping')
        return {"status": "success", "message": "Successfully connected to MongoDB."}
    except Exception as e:
        return {"status": "error", "message": str(e)}