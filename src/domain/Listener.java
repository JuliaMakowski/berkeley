package domain;

import java.net.DatagramPacket;
import java.util.Arrays;

public abstract class Listener extends Thread {

    protected abstract void execute();

    @Override
    public void run() {
        this.execute();
    }

    protected String removeNull(DatagramPacket packet){
        byte[] data = packet.getData();
        String s = new String(packet.getData(), packet.getOffset(), packet.getData().length);
        int i = 0;
        char[] c = s.toCharArray();
        while (i < data.length) {
            if (packet.getData()[i] != 0) {
                i++;
            } else {
                break;
            }
        }
        byte[] result;
        if (i > 0 && i < data.length) {
            result = Arrays.copyOfRange(data,0, i);
        } else {
            result = data;
        }
        return new String(result);
    }
}
