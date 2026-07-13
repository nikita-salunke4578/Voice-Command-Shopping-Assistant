import { create } from 'zustand';

const useVoiceStore = create((set) => ({
  isListening: false,
  isProcessing: false,
  transcript: '',
  interimTranscript: '',
  lastResponse: null,
  error: null,
  language: 'en-US',

  setListening: (isListening) => set({ isListening }),
  setProcessing: (isProcessing) => set({ isProcessing }),
  setTranscript: (transcript) => set({ transcript }),
  setInterimTranscript: (interimTranscript) => set({ interimTranscript }),
  setLastResponse: (lastResponse) => set({ lastResponse }),
  setError: (error) => set({ error }),
  setLanguage: (language) => set({ language }),

  reset: () => set({
    isListening: false,
    isProcessing: false,
    transcript: '',
    interimTranscript: '',
    error: null,
  }),
}));

export default useVoiceStore;
