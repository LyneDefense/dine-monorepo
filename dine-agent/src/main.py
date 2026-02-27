from fastapi import FastAPI
from src.api.routes import router
from src.config import get_settings

settings = get_settings()

app = FastAPI(
    title="Dine Agent",
    description="AI-powered restaurant phone ordering agent",
    version="0.1.0"
)

app.include_router(router)


@app.get("/health")
async def health_check():
    return {"status": "healthy"}


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "src.main:app",
        host=settings.host,
        port=settings.port,
        reload=settings.debug
    )
