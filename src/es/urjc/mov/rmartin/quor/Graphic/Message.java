package es.urjc.mov.rmartin.quor.Graphic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public abstract class Message {
    public enum MessageTypes {
        LOGIN, PLAY, OK, OKLOGIN, ERROR,POSITION
    }

    private static final MessageTypes[] messages = MessageTypes.values();

    @Override
	public String toString() {
		return "Message [type()=" + type() + ", toString()=" + super.toString() + "]";
	}

	public abstract MessageTypes type();

    public abstract void writeTo(DataOutputStream odata);

    public static Message ReadFrom(DataInputStream idata) {
        Message message = null;
        try {
            int msg_type = idata.readInt();
            switch (messages[msg_type]) {
                case LOGIN:
                    message = new Login(idata);
                    break;
                case PLAY:
                    message = new Play(idata);
                    break;
                case OK:
                    message = new OkMessage();
                    break;
                case OKLOGIN:
                	message = new OkLogin(idata);
                	break;
                case ERROR:
                    message = new ErrorMessage();
                    break;
                case POSITION:
                    message = new PositionMessage(idata);
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
    
    public static class OkLogin extends Message{
    	private static final MessageTypes TMSG = MessageTypes.OKLOGIN;
    	int turno;
    	
    	OkLogin(DataInputStream idata) throws IOException{
    		this.turno=idata.readInt();
    	}
    	
    	OkLogin(int turno){
    		this.turno=turno;
    	}

		@Override
		public MessageTypes type() {
			return TMSG;
		}
		   
		@Override
		public void writeTo(DataOutputStream odata) {
			try {
				odata.writeInt(type().ordinal());
				odata.writeInt(turno);
				odata.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}    	
    }

    public static class Login extends Message {

        private static final MessageTypes TMSG = MessageTypes.LOGIN;
        String nick;

        Login(DataInputStream idata) throws IOException {
            byte[] buffer=new byte[idata.readInt()];
            idata.readFully(buffer);
            this.nick=new String(buffer,"UTF-8");
        }
        Login(String nick){
            this.nick=nick;
        }

        public String getNick() {
			return nick;
		}
		public void setNick(String nick) {
			this.nick = nick;
		}
		public static MessageTypes getTmsg() {
			return TMSG;
		}
		@Override
        public MessageTypes type() {
            return TMSG;
        }

        public void writeTo(DataOutputStream odata) {
            try {
                odata.writeInt(type().ordinal());
                byte buf[] = nick.getBytes();
                odata.writeInt(buf.length);
                odata.write(buf,0,buf.length);
                odata.flush();
            } catch (IOException e) {
                throw new RuntimeException(this + "write: " + e);
            }
        }
    }
    public static class Play extends Message{
        private static final MessageTypes TMSG = MessageTypes.PLAY;
        String nick;
        int x;
        int y;
        Boolean type;

        public String getNick() {
			return nick;
		}
		public void setNick(String nick) {
			this.nick = nick;
		}
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		public Boolean getType() {
			return type;
		}
		public void setType(Boolean type) {
			this.type = type;
		}
		Play(DataInputStream idata) throws IOException {
        	byte[] buffer=new byte[idata.readInt()];
            idata.readFully(buffer);
            this.nick=new String(buffer,"UTF-8");
            this.x=idata.readInt();
            this.y=idata.readInt();
            this.type = idata.readBoolean();
        }
        Play(String nick,int x,int y,Boolean type){
        	this.nick=nick;
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
                byte buf[] = nick.getBytes();
                odata.writeInt(buf.length);
                odata.write(buf,0,buf.length);
                odata.writeInt(x);
                odata.writeInt(y);
                odata.writeBoolean(type);
                odata.flush();
            }catch (IOException e){
                throw new RuntimeException(this + "write: " + e);
            }
        }
    }
    public static class PositionMessage extends Message{
        private static final MessageTypes TMSG = MessageTypes.POSITION;
        String nick;
        double longitude;
        double latitude;

        public PositionMessage(String nick, double latitude, double longitude) throws IOException{
            this.nick=nick;
            this.latitude=latitude;
            this.longitude=longitude;
        }

        PositionMessage(DataInputStream idata) throws IOException {
            byte[] buffer=new byte[idata.readInt()];
            idata.readFully(buffer);
            this.nick=new String(buffer,"UTF-8");
            this.latitude = idata.readDouble();
            this.longitude = idata.readDouble();
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

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		@Override
        public void writeTo(DataOutputStream odata) {
            try {
                odata.writeInt(MessageTypes.POSITION.ordinal());
                byte buf[] = nick.getBytes();
                odata.writeInt(buf.length);
                odata.write(buf,0,buf.length);
                odata.writeDouble(latitude);
                odata.writeDouble(longitude);
                odata.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
