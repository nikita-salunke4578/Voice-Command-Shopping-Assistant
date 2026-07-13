import api from './api';

export const voiceService = {
  processCommand: (text, language = 'en', listId = null) =>
    api.post('/voice/process', { text, language, listId }),
};
