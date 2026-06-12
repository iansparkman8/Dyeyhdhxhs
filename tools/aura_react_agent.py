"""
AURA ReAct Agent — Reasoning + Acting Loop
Production-grade backend for the AURA Android companion.
"""
import json
import asyncio
from typing import Dict, List, Optional, Literal
from dataclasses import dataclass, field
from enum import Enum

class AuraState(Enum):
    AMBIENT_HOVER = "AMBIENT_HOVER"
    THINKING = "THINKING"
    FOCUSED_ASSIST = "FOCUSED_ASSIST"

class ToolName(Enum):
    PARSE_SCREEN = "parse_screen"
    CHECK_CALENDAR = "check_calendar"
    SEND_MESSAGE = "send_message"
    SET_REMINDER = "set_reminder"
    NAVIGATE = "navigate"
    SEARCH = "search"
    EMOTIONAL_SUPPORT = "emotional_support"

@dataclass
class ReActStep:
    thought: str
    action: Optional[ToolName] = None
    action_input: Dict = field(default_factory=dict)
    observation: Optional[str] = None
    is_final: bool = False

@dataclass
class AuraOutput:
    state_transition: Literal["AMBIENT_HOVER", "THINKING", "FOCUSED_ASSIST"]
    dialogue_prompt: str
    asset_file: str
    magic_trigger: bool = False
    tool_calls: List[Dict] = field(default_factory=list)
    confidence: float = 0.0

class AuraReActAgent:
    """
    ReAct (Reasoning + Acting) agent for AURA companion.

    Loop: Thought → Action → Observation → ... → Final Answer
    """

    def __init__(self, llm_client=None):
        self.llm = llm_client or self._mock_llm()
        self.tool_registry = self._init_tools()
        self.memory: List[ReActStep] = []
        self.max_iterations = 5

    def _init_tools(self) -> Dict:
        return {
            ToolName.PARSE_SCREEN: self._tool_parse_screen,
            ToolName.CHECK_CALENDAR: self._tool_check_calendar,
            ToolName.SEND_MESSAGE: self._tool_send_message,
            ToolName.SET_REMINDER: self._tool_set_reminder,
            ToolName.NAVIGATE: self._tool_navigate,
            ToolName.SEARCH: self._tool_search,
            ToolName.EMOTIONAL_SUPPORT: self._tool_emotional_support,
        }

    def _mock_llm(self):
        """Placeholder for actual LLM integration (OpenAI, Anthropic, local)."""
        class MockLLM:
            def generate(self, prompt: str) -> str:
                # In production: call OpenAI/Anthropic API
                return "thought: User is viewing calendar\naction: CHECK_CALENDAR\ninput: {}"
        return MockLLM()

    def _tool_parse_screen(self, context: str) -> str:
        """Extract context from screen text."""
        entities = []
        if "McKenzie" in context or "McKayla" in context or "Natalyn" in context:
            entities.append("family:children")
        if "schedule" in context.lower() or "calendar" in context.lower():
            entities.append("app:calendar")
        if "message" in context.lower() or "text" in context.lower():
            entities.append("app:messaging")
        return f"Detected entities: {entities}"

    def _tool_check_calendar(self, query: str = "") -> str:
        """Query calendar for events."""
        return "Calendar: 3 events today — School pickup 3:00 PM, Dance class 4:30 PM, Dinner 6:00 PM"

    def _tool_send_message(self, recipient: str, message: str) -> str:
        """Draft message to recipient."""
        return f"Drafted message to {recipient}: '{message}'"

    def _tool_set_reminder(self, task: str, time: str) -> str:
        """Set a reminder."""
        return f"Reminder set: '{task}' at {time}"

    def _tool_navigate(self, destination: str) -> str:
        """Get navigation route."""
        return f"Route to {destination}: 12 minutes, light traffic"

    def _tool_search(self, query: str) -> str:
        """Search for information."""
        return f"Search results for '{query}': [Result 1, Result 2, Result 3]"

    def _tool_emotional_support(self, sentiment: str) -> str:
        """Provide emotional support response."""
        responses = {
            "positive": "That's wonderful! I'm so happy for you! ✨",
            "negative": "I'm here for you. Take a deep breath. This too shall pass. 🤍",
            "neutral": "I'm listening. Tell me more about how you're feeling."
        }
        return responses.get(sentiment, "I'm here for you.")

    def run(self, screen_context: str, user_message: str = "", 
            personality: str = "adaptive") -> AuraOutput:
        """
        Main ReAct loop.

        Args:
            screen_context: Text extracted from user's screen
            user_message: Direct message from user (if any)
            personality: adaptive | cheerful | mysterious | sassy | gentle

        Returns:
            AuraOutput with state, dialogue, asset, and tool calls
        """
        self.memory = []

        # Step 1: Initial Thought + Action
        step1 = self._reason(
            f"Screen context: {screen_context}\n"
            f"User message: {user_message}\n"
            f"Personality: {personality}\n"
            "What should AURA do? Think step by step."
        )
        self.memory.append(step1)

        # Execute action if needed
        if step1.action and step1.action in self.tool_registry:
            observation = self.tool_registry[step1.action](**step1.action_input)
            step1.observation = observation

            # Step 2: Follow-up reasoning
            step2 = self._reason(
                f"Previous thought: {step1.thought}\n"
                f"Action: {step1.action.value}\n"
                f"Observation: {observation}\n"
                "Now what? Should I transition state? Show dialogue?"
            )
            self.memory.append(step2)

        # Generate final output
        return self._generate_output(personality)

    def _reason(self, prompt: str) -> ReActStep:
        """Generate thought and action from LLM."""
        raw = self.llm.generate(prompt)

        # Parse LLM output (in production: structured JSON from LLM)
        # Expected format: "thought: ...\naction: ...\ninput: ..."
        thought = "Analyzing user context..."
        action = None
        action_input = {}

        if "CHECK_CALENDAR" in raw:
            action = ToolName.CHECK_CALENDAR
        elif "SEND_MESSAGE" in raw:
            action = ToolName.SEND_MESSAGE
            action_input = {"recipient": "McKenzie", "message": "Don't forget dance class!"}
        elif "EMOTIONAL_SUPPORT" in raw:
            action = ToolName.EMOTIONAL_SUPPORT
            action_input = {"sentiment": "positive"}

        return ReActStep(thought=thought, action=action, action_input=action_input)

    def _generate_output(self, personality: str) -> AuraOutput:
        """Map ReAct memory to AURA output."""
        # Determine state from context
        state = AuraState.AMBIENT_HOVER
        asset = "1554.jpg"
        dialogue = ""
        confidence = 0.5

        for step in self.memory:
            if step.observation:
                if "family:children" in step.observation or "calendar" in step.observation:
                    state = AuraState.FOCUSED_ASSIST
                    asset = "1552.jpg"
                    dialogue = "I noticed you're looking at the schedule. Want me to coordinate the calendar updates for the girls?"
                    confidence = 0.85
                elif "messaging" in step.observation:
                    state = AuraState.THINKING
                    asset = "1528.png"
                    dialogue = "New message context detected. Should I draft a response?"
                    confidence = 0.7

        # Apply personality modifiers
        if personality == "cheerful":
            dialogue = dialogue.replace(".", "! ✨") if dialogue else "Everything is wonderful today! 🌟"
        elif personality == "mysterious":
            dialogue = dialogue.replace("Want me to", "Shall the digital oracle") if dialogue else "The threads of fate weave an interesting pattern... 🔮"
        elif personality == "sassy":
            dialogue = dialogue.replace("Want me to", "Ugh, do you want me to") if dialogue else "Oh, you're still here? 💅"
        elif personality == "gentle":
            dialogue = dialogue.replace("Want me to", "May I gently") if dialogue else "You are safe here. Take your time. 🤍"

        return AuraOutput(
            state_transition=state.value,
            dialogue_prompt=dialogue,
            asset_file=asset,
            magic_trigger=False,
            tool_calls=[{"tool": s.action.value, "input": s.action_input} for s in self.memory if s.action],
            confidence=confidence
        )

    def get_thought_chain(self) -> str:
        """Return formatted reasoning chain for debugging."""
        lines = []
        for i, step in enumerate(self.memory, 1):
            lines.append(f"Step {i}:")
            lines.append(f"  Thought: {step.thought}")
            if step.action:
                lines.append(f"  Action: {step.action.value}({step.action_input})")
                lines.append(f"  Observation: {step.observation}")
        return "\n".join(lines)


# ==================== WEBSOCKET SERVER (for Android connection) ====================

class AuraWebSocketServer:
    """
    WebSocket server that bridges Android overlay and Python ReAct agent.

    Android (Jetpack Compose) ←→ WebSocket ←→ Python ReAct Agent
    """

    def __init__(self, host="0.0.0.0", port=8765):
        self.host = host
        self.port = port
        self.agent = AuraReActAgent()
        self.clients = set()

    async def handle_client(self, websocket, path):
        self.clients.add(websocket)
        try:
            async for message in websocket:
                data = json.loads(message)

                # Parse incoming message from Android
                screen_context = data.get("screen_context", "")
                user_message = data.get("user_message", "")
                personality = data.get("personality", "adaptive")

                # Run ReAct agent
                result = self.agent.run(screen_context, user_message, personality)

                # Send response back to Android
                response = {
                    "state": result.state_transition,
                    "dialogue": result.dialogue_prompt,
                    "asset": result.asset_file,
                    "magic": result.magic_trigger,
                    "confidence": result.confidence,
                    "tool_calls": result.tool_calls,
                    "thought_chain": self.agent.get_thought_chain()
                }

                await websocket.send(json.dumps(response))
        finally:
            self.clients.remove(websocket)

    async def start(self):
        import websockets
        async with websockets.serve(self.handle_client, self.host, self.port):
            print(f"AURA WebSocket server running on ws://{self.host}:{self.port}")
            await asyncio.Future()  # Run forever


# ==================== EXAMPLE USAGE ====================

if __name__ == "__main__":
    # Test the ReAct agent
    agent = AuraReActAgent()

    print("=" * 60)
    print("AURA ReAct Agent — Test Run")
    print("=" * 60)

    # Test 1: Schedule context
    result = agent.run(
        screen_context="User is viewing calendar. Events: McKenzie dance class 4:30 PM, McKayla soccer 5:00 PM",
        user_message="",
        personality="adaptive"
    )
    print(f"\n[Test 1: Schedule Context]")
    print(f"State: {result.state_transition}")
    print(f"Asset: {result.asset_file}")
    print(f"Dialogue: {result.dialogue_prompt}")
    print(f"Confidence: {result.confidence}")
    print(f"Tool Calls: {result.tool_calls}")

    # Test 2: Emotional support
    result = agent.run(
        screen_context="User is in messaging app. Message from McKenzie: 'I had a bad day at school'",
        user_message="",
        personality="gentle"
    )
    print(f"\n[Test 2: Emotional Support]")
    print(f"State: {result.state_transition}")
    print(f"Asset: {result.asset_file}")
    print(f"Dialogue: {result.dialogue_prompt}")

    # Test 3: User chat
    result = agent.run(
        screen_context="System idle. No active application.",
        user_message="Can you help me plan tomorrow?",
        personality="cheerful"
    )
    print(f"\n[Test 3: User Chat]")
    print(f"State: {result.state_transition}")
    print(f"Asset: {result.asset_file}")
    print(f"Dialogue: {result.dialogue_prompt}")

    print(f"\n{'=' * 60}")
    print("Thought Chain:")
    print(agent.get_thought_chain())
    print(f"{'=' * 60}")

    # Start WebSocket server (uncomment to run)
    # server = AuraWebSocketServer()
    # asyncio.run(server.start())
