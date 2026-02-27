from twilio.rest import Client
from src.config import get_settings


class TwilioService:
    def __init__(self):
        settings = get_settings()
        self.client = Client(
            settings.twilio_account_sid,
            settings.twilio_auth_token
        )
        self.phone_number = settings.twilio_phone_number

    def make_call(self, to: str, url: str):
        """Make an outbound call"""
        call = self.client.calls.create(
            to=to,
            from_=self.phone_number,
            url=url
        )
        return call.sid

    def send_sms(self, to: str, body: str):
        """Send an SMS message"""
        message = self.client.messages.create(
            to=to,
            from_=self.phone_number,
            body=body
        )
        return message.sid
