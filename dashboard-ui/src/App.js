import React, { useState } from 'react';
import './App.css';

function App() {
  const [sampleId, setSampleId] = useState('HG001');
  const [pipelineStatus, setPipelineStatus] = useState('Idle');
  const [predictionResult, setPredictionResult] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleStartPipeline = async () => {
    setPipelineStatus('Starting...');
    setIsLoading(true);
    setPredictionResult(null);

    try {
      // This API call triggers the entire backend pipeline
      await fetch('http://localhost:8080/api/ingest', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          fileUri: `/data/raw/SRR062634_1.fastq`,
          sampleId: sampleId,
          fileType: 'FASTQ'
        })
      });

      // Simulate monitoring the pipeline's progress
      setPipelineStatus('Processing (Alignment, Variant Calling, Persistence)...');
      // In a real app, this would be updated via WebSockets or polling
      setTimeout(() => {
        setPipelineStatus(`Complete for ${sampleId}`);
        setIsLoading(false);
      }, 15000); // Wait 15 seconds to simulate the backend work

    } catch (error) {
      setPipelineStatus('Error starting pipeline.');
      setIsLoading(false);
    }
  };

  const handleGetPrediction = async () => {
    setIsLoading(true);
    setPredictionResult(null);
    try {
      const response = await fetch('http://localhost:8001/predict', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sample_id: sampleId })
      });

      if (!response.ok) {
        throw new Error('Model or sample not found. Ensure pipeline has run and model is trained.');
      }

      const data = await response.json();
      setPredictionResult(data);
    } catch (error) {
      setPredictionResult({ error: error.message });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>AI-Driven Bioinformatics Platform</h1>
        <div className="card">
          <h2>Sample Analysis</h2>
          <div className="input-group">
            <label htmlFor="sampleId">Sample ID:</label>
            <input
              type="text"
              id="sampleId"
              value={sampleId}
              onChange={(e) => setSampleId(e.target.value)}
            />
          </div>
          <div className="button-group">
            <button onClick={handleStartPipeline} disabled={isLoading}>
              {pipelineStatus.includes('Processing') ? 'Pipeline Running...' : 'Start Analysis Pipeline'}
            </button>
            <button onClick={handleGetPrediction} disabled={isLoading}>
              Get Prediction
            </button>
          </div>
        </div>

        <div className="card">
          <h2>Results</h2>
          <p><strong>Pipeline Status:</strong> {pipelineStatus}</p>
          {predictionResult && (
            <div className="results-display">
              {predictionResult.error ? (
                <p className="error">Error: {predictionResult.error}</p>
              ) : (
                <>
                  <p><strong>Prediction for {predictionResult.sample_id}:</strong> <span className={predictionResult.prediction}>{predictionResult.prediction}</span></p>
                  <p><strong>Confidence Score:</strong> {(predictionResult.confidence_score * 100).toFixed(2)}%</p>
                </>
              )}
            </div>
          )}
        </div>
      </header>
    </div>
  );
}

export default App;