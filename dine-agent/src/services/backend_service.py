import httpx
from src.config import get_settings


class BackendService:
    def __init__(self):
        settings = get_settings()
        self.base_url = settings.backend_api_url

    async def get_restaurant(self, restaurant_id: int) -> dict:
        """Get restaurant information"""
        async with httpx.AsyncClient() as client:
            response = await client.get(
                f"{self.base_url}/restaurants/{restaurant_id}"
            )
            response.raise_for_status()
            return response.json()

    async def get_menu(self, restaurant_id: int) -> dict:
        """Get restaurant menu"""
        async with httpx.AsyncClient() as client:
            response = await client.get(
                f"{self.base_url}/restaurants/{restaurant_id}/menu"
            )
            response.raise_for_status()
            return response.json()

    async def create_order(self, restaurant_id: int, order_data: dict) -> dict:
        """Create a new order"""
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{self.base_url}/restaurants/{restaurant_id}/orders",
                json=order_data
            )
            response.raise_for_status()
            return response.json()

    async def get_ai_settings(self, restaurant_id: int) -> dict:
        """Get AI phone settings for restaurant"""
        async with httpx.AsyncClient() as client:
            response = await client.get(
                f"{self.base_url}/restaurants/{restaurant_id}/ai-phone-settings"
            )
            response.raise_for_status()
            return response.json()
