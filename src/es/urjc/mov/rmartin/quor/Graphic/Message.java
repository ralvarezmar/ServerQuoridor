package es.urjc.mov.rmartin.quor.Graphic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public abstract class Message {
    public enum MessageTypes {
        LOGIN, PLAY, OK, ERROR
    }

    private static final MessageTypes[] messages = MessageTypes.values();

    public abstract MessageTypes type();

    public abstract void writeTo(DataOutputStream odata);
    

    @Override
	public String toString() {
		return "Message [type()=" + type() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

	public static Message ReadFrom(DataInputStream idata) {
        Message message = null;
        try {
            int msg_type = idata.readInt();
            switch (messages[msg_type]) {
                case LOGIN:
                    String nick=idata.readUTF();
                    message = new Login(nick);
                    break;
                case PLAY:
                    int x=idata.readInt();
                    int y=idata.readInt();
                    Boolean type = idata.readBoolean();
                    message = new Play(x,y,type);
                    break;
                case OK:
                    message = new OkMessage();
                    break;
                case ERROR:
                    message = new ErrorMessage();
                    break;
                default:
                    break;
            }
            return message;
        } catch (EOFException e) {
            throw new RuntimeException("EOF");
        } catch (IOException e) {
            throw new RuntimeException("Msg: read:" + e);
        }
    }

    public static class ErrorMessage extends Message {
        private static final MessageTypes RMSG = MessageTypes.ERROR;

        @Override
        public MessageTypes type() {
            return RMSG;
        }

        public void writeTo(DataOutputStream odata) {
            try {
                odata.writeInt(type().ordinal());
                odata.flush();
            } catch (IOException e) {
                throw new RuntimeException(this + "write: " + e);
            }
        }
    }

    public static class OkMessage extends Message {
        private static final MessageTypes RMSG = MessageTypes.OK;

        @Override
        public MessageTypes type() {
            return RMSG;
        }

        public void writeTo(DataOutputStream odata) {
            try {
                odata.writeInt(type().ordinal());
                odata.flush();
            } catch (IOException e) {
                throw new RuntimeException(this + "write: " + e);
            }
        }
    }

    public static class Login extends Message {
        private static final MessageTypes TMSG = MessageTypes.LOGIN;
        String nick;
        Login(String nick){
            this.nick=nick;
        }
        @Override
        public MessageTypes type() {
            return TMSG;
        }        

        public String getNick() {
			return nick;
		}
        
		public void setNick(String nick) {
			this.nick = nick;
		}
		public void writeTo(DataOutputStream odata) {
            try {
                odata.writeInt(type().ordinal());
                byte buf[] = nick.getBytes();
                odata.write(buf,0,buf.length);
                odata.flush();
            } catch (IOException e) {
                throw new RuntimeException(this + "write: " + e);
            }
        }
    }
    
    public static class Play extends Message{

        private static final MessageTypes TMSG = MessageTypes.PLAY;
        int x;
        int y;
        Boolean type;

        Play(int x,int y,Boolean type){
            this.x=x;
            this.y=y;
            this.type=type;
        }
        @Override
        public MessageTypes type() {
            return TMSG;
        }

        public void writeTo(DataOutputStream odata){
            try{
                odata.writeInt(type().ordinal());
                odata.writeInt(x);
                odata.writeInt(y);
                odata.writeBoolean(type);
                odata.flush();
            }catch (IOException e){
                throw new RuntimeException(this + "write: " + e);
            }
        }
    }
}
