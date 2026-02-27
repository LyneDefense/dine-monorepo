from fastapi import APIRouter, Request, Response
from twilio.twiml.voice_response import VoiceResponse

router = APIRouter()


@router.post("/twilio/voice")
async def handle_incoming_call(request: Request):
    """Handle incoming Twilio voice call"""
    response = VoiceResponse()
    response.say(
        "Welcome to Dine AI. How can I help you today?",
        voice="alice"
    )
    # TODO: Implement actual conversation flow
    return Response(content=str(response), media_type="application/xml")


@router.post("/twilio/voice/callback")
async def handle_voice_callback(request: Request):
    """Handle Twilio voice callback"""
    form_data = await request.form()
    speech_result = form_data.get("SpeechResult", "")

    response = VoiceResponse()
    # TODO: Process speech and generate response using LLM
    response.say(f"I heard: {speech_result}", voice="alice")
    return Response(content=str(response), media_type="application/xml")
