import javafx.application.Platform;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.util.function.Consumer;

public class SerialPortTest {

    static SerialPort serialPort;

    public static void main(String[] args) {
        serialPort = new SerialPort("COM3");
        try {
            serialPort.openPort();

            //Set up parameters for Serial Port
            //Normally, the baud rate is 9600, and default data, stop and parity on Arduino is 8, 1, 0.
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            //Preparing a mask. In a mask, we need to specify the types of events that we want to track.
            //Well, for example, we need to know what came some data, thus in the mask must have the
            //following value: MASK_RXCHAR. If we, for example, still need to know about changes in states
            //of lines CTS and DSR, the mask has to look like this: SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;

            //Set the prepared mask
            serialPort.setEventsMask(mask);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                                          SerialPort.FLOWCONTROL_RTSCTS_OUT);
            //Add an interface through which we will receive information about eventsserialPort.addEventListener(

            serialPort.addEventListener(new SerialPortReader(), SerialPort.MASK_RXCHAR);
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

    private static class SerialPortReader implements SerialPortEventListener {

        @Override
        public void serialEvent(SerialPortEvent event) {

            //To read the event in RXCHAR type
            if (event.isRXCHAR()) {

                //Notice!!!: the number in the equality below has to be the same as
                //the length of bytes sent from Arduino (including "\r")
                //FYI, it's suggested that don't use "println" method in Arduino,
                //rather "print" and "\r". Because "println" can cause strange error,
                //such as printing incomplete lines.
                if (event.getEventValue() == 5) {
                    try {
                        String serialStringFragment = serialPort.readString();
                        System.out.print(serialStringFragment);
                    } catch (SerialPortException ex) {
                        System.out.println(ex);
                    }
                }
            }
        }
    }
}