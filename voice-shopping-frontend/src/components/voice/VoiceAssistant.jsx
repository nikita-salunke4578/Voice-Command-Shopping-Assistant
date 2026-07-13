import { useEffect, useCallback, useRef } from 'react';
import useVoiceStore from '../../store/useVoiceStore';
import useShoppingStore from '../../store/useShoppingStore';
import { voiceService } from '../../services/voiceService';
import { useToast } from '../common/useToast';

export default function VoiceAssistant() {
  const {
    isListening,
    isProcessing,
    transcript,
    interimTranscript,
    language,
    setLanguage,
    setListening,
    setProcessing,
    setTranscript,
    setInterimTranscript,
    reset,
  } = useVoiceStore();

  const { activeList, setItems, setSearchResults, setSuggestions, setModalMessage, setCommandAcknowledgement, setVoiceErrorMessage } = useShoppingStore();
  const { addToast } = useToast();
  const recognitionRef = useRef(null);

  // Initialize Speech Recognition
  useEffect(() => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
      addToast('Speech recognition is not supported in this browser. Please use Chrome or Edge.', 'error');
      return;
    }

    const recognition = new SpeechRecognition();
    recognition.continuous = false; // Stop when the user stops speaking
    recognition.interimResults = true;
    recognition.lang = language;

    recognition.onstart = () => {
      setListening(true);
      setTranscript('');
      setInterimTranscript('');
    };

    recognition.onresult = (event) => {
      let currentTranscript = '';
      let currentInterim = '';

      for (let i = event.resultIndex; i < event.results.length; i++) {
        const result = event.results[i];
        if (result.isFinal) {
          currentTranscript += result[0].transcript;
        } else {
          currentInterim += result[0].transcript;
        }
      }
      
      if (currentTranscript) setTranscript(currentTranscript);
      setInterimTranscript(currentInterim);
    };

    recognition.onerror = (event) => {
      console.error('Speech recognition error:', event.error);
      setListening(false);
      
      if (event.error === 'not-allowed') {
        addToast('Please allow microphone access to use voice commands.', 'error');
      } else if (event.error !== 'no-speech') {
        addToast(`Voice recognition error: ${event.error}`, 'error');
      }
    };

    recognition.onend = () => {
      setListening(false);
    };

    recognitionRef.current = recognition;

    return () => {
      if (recognitionRef.current) {
        recognitionRef.current.abort();
      }
    };
  }, [language, setListening, setTranscript, setInterimTranscript, addToast]);

  // Process completed transcript
  const processCommand = useCallback(async (textToProcess) => {
    if (!textToProcess.trim() || !activeList) return;

    setProcessing(true);
    try {
      // Send text to backend Groq API
      const response = await voiceService.processCommand(textToProcess, language, activeList.id);
      
      // Update store with new items list, search results, and suggestions
      if (response.data) {
        if (response.data.updatedItems) {
          setItems(response.data.updatedItems);
        }
        if (response.data.searchResults) setSearchResults(response.data.searchResults);
        if (response.data.suggestions) setSuggestions(response.data.suggestions);

        if (!response.data.success) {
          setVoiceErrorMessage(response.data.message || 'No matching item was found in your list.');
          return;
        }

        // Only show "Item Unavailable" modal when the backend explicitly says items are unavailable
        if (response.data.hasUnavailableItems && response.data.message) {
          setModalMessage(response.data.message);
        } else if (['ADD', 'REMOVE'].includes((response.data.action || '').toUpperCase())) {
          setCommandAcknowledgement(response.data.message || 'Your shopping list has been updated.');
        } else {
          addToast(response.data.message || 'Command processed successfully', 'success');
        }
      }
    } catch (error) {
      console.error('Failed to process command:', error);
      setVoiceErrorMessage(error.message || 'Failed to process your voice command. Please try again.');
    } finally {
      setProcessing(false);
      setTimeout(() => reset(), 2000); // Clear transcript after 2 seconds
    }
  }, [activeList, language, setItems, setSearchResults, setSuggestions, setModalMessage, setCommandAcknowledgement, setVoiceErrorMessage, addToast, setProcessing, reset]);

  const processingRef = useRef(false);

  // Trigger processing when listening stops and we have a final transcript
  useEffect(() => {
    if (!isListening && transcript && !isProcessing && !processingRef.current) {
      processingRef.current = true;
      processCommand(transcript).finally(() => {
        processingRef.current = false;
      });
    }
  }, [isListening, transcript, isProcessing]); // eslint-disable-line react-hooks/exhaustive-deps

  const toggleListening = () => {
    if (isProcessing) return; // Don't allow new recording while processing

    if (isListening) {
      recognitionRef.current?.stop();
    } else {
      if (!activeList) {
        addToast('Please select or create a shopping list first', 'warning');
        return;
      }
      try {
        recognitionRef.current?.start();
      } catch (e) {
        console.error('Failed to start recognition:', e);
      }
    }
  };

  return (
    <div className="voice-section">
      {(transcript || interimTranscript || isProcessing) && (
        <div className="voice-transcript">
          {transcript && <span>{transcript}</span>}
          {interimTranscript && <span className="interim"> {interimTranscript}</span>}
          {isProcessing && <span className="interim">...processing AI</span>}
        </div>
      )}

      {isListening && (
        <div className="voice-visualizer">
          {[...Array(7)].map((_, i) => (
            <div key={i} className="voice-bar"></div>
          ))}
        </div>
      )}

      <div className="voice-btn-wrapper">
        <button
          className={`voice-btn ${isListening ? 'listening' : ''} ${isProcessing ? 'processing' : ''}`}
          onClick={toggleListening}
          disabled={isProcessing}
          aria-label={isListening ? 'Stop listening' : 'Start voice command'}
        >
          {isProcessing ? '✨' : '🎤'}
        </button>
        {isListening && <div className="voice-btn-ring"></div>}
      </div>
      <span className="voice-label">
        {isProcessing ? 'Thinking...' : isListening ? 'Listening...' : 'Tap to speak'}
      </span>
      <div style={{ marginTop: '8px' }}>
        <select 
          value={language} 
          onChange={(e) => setLanguage(e.target.value)}
          style={{ background: 'transparent', color: 'white', border: '1px solid rgba(255,255,255,0.2)', borderRadius: '4px', padding: '2px 4px', fontSize: '0.8rem' }}
        >
          <option value="en-US" style={{ color: '#000' }}>English</option>
          <option value="hi-IN" style={{ color: '#000' }}>हिन्दी (Hindi)</option>
          <option value="mr-IN" style={{ color: '#000' }}>मराठी (Marathi)</option>
          <option value="ta-IN" style={{ color: '#000' }}>தமிழ் (Tamil)</option>
          <option value="te-IN" style={{ color: '#000' }}>తెలుగు (Telugu)</option>
          <option value="kn-IN" style={{ color: '#000' }}>ಕನ್ನಡ (Kannada)</option>
          <option value="es-ES" style={{ color: '#000' }}>Español (Spanish)</option>
          <option value="fr-FR" style={{ color: '#000' }}>Français (French)</option>
          <option value="de-DE" style={{ color: '#000' }}>Deutsch (German)</option>
        </select>
      </div>
    </div>
  );
}
