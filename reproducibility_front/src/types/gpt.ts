export interface ChatMessage {
    role: 'user' | 'assistant' | 'system';
    content: string;
  }
  
  export interface ChatResponse {
    id: string;
    messages: ChatMessage[];
    created: number;
    model: string;
    object: 'text_completion';
    choices: {
      text: string;
      index: number;
      finish_reason: 'length' | 'stop';
    }[];
  }
  