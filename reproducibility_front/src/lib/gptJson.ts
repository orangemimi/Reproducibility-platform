import type { ChatMessage } from '@/types';
import OpenAI from 'openai';

const openai = new OpenAI({
  apiKey: 'sb-ccc08d92c8d641e071e73c047508bce558f84178e7906c45',
  baseURL: 'https://api.openai-sb.com/v1',
  dangerouslyAllowBrowser: true,
});

export async function chatJson(messageList: ChatMessage[]): Promise<Response> {
  const response = await openai.chat.completions.create({
    model: 'gpt-3.5-turbo',
    stream: false,
    messages: messageList,
    response_format: { type: 'json_object' },
  });

  return response as unknown as Response;
}
