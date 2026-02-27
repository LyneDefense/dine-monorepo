from openai import OpenAI
from src.config import get_settings


class LLMService:
    def __init__(self):
        settings = get_settings()
        self.client = OpenAI(api_key=settings.openai_api_key)
        self.model = "gpt-4"

    async def generate_response(
        self,
        user_message: str,
        system_prompt: str,
        conversation_history: list = None
    ) -> str:
        """Generate a response using the LLM"""
        messages = [{"role": "system", "content": system_prompt}]

        if conversation_history:
            messages.extend(conversation_history)

        messages.append({"role": "user", "content": user_message})

        response = self.client.chat.completions.create(
            model=self.model,
            messages=messages,
            temperature=0.7,
            max_tokens=500
        )

        return response.choices[0].message.content

    def get_restaurant_system_prompt(self, restaurant_info: dict) -> str:
        """Generate system prompt for restaurant ordering"""
        return f"""You are a friendly AI phone assistant for {restaurant_info.get('name', 'the restaurant')}.
Your job is to help customers place orders and make reservations.

Restaurant Information:
- Name: {restaurant_info.get('name', 'N/A')}
- Address: {restaurant_info.get('address', 'N/A')}
- Phone: {restaurant_info.get('phone', 'N/A')}

Guidelines:
- Be polite and professional
- Confirm orders before finalizing
- Ask about allergies when relevant
- Offer recommendations when asked
- Keep responses concise for phone conversation
"""
