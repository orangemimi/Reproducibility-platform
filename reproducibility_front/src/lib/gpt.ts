import type { ChatMessage } from '@/types';

export async function chat(messageList: ChatMessage[]) {
  return fetch('https://api.openai-sb.com/v1/chat/completions', {
    method: 'post',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer sb-ccc08d92c8d641e071e73c047508bce558f84178e7906c45`,
      
    },
    body: JSON.stringify({
      model: 'gpt-3.5-turbo',
      // model: 'gpt-4o',
      stream: true,
      messages: messageList,
    }),
  });
}
