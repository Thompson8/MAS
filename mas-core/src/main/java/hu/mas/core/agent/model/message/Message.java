package hu.mas.core.agent.model.message;

import java.util.concurrent.SynchronousQueue;

public class Message {

	private final String agentId;

	private final MessageType type;

	private final MessageBody body;

	private final SynchronousQueue<Message> connection;

	public Message(String agentId, MessageType type, MessageBody body) {
		this.agentId = agentId;
		this.type = type;
		this.body = body;
		this.connection = new SynchronousQueue<>();
	}

	public String getAgentId() {
		return agentId;
	}

	public MessageType getType() {
		return type;
	}

	public MessageBody getBody() {
		return body;
	}

	public SynchronousQueue<Message> getConnection() {
		return connection;
	}

	@Override
	public String toString() {
		return "Message [agentId=" + agentId + ", type=" + type + ", body=" + body + ", connection=" + connection + "]";
	}

}
