package pilots.runtime.errsig;

import java.util.Arrays;

public class SlidingWindow {
    private static final int MAX_WINDOW_SIZE = 1024; // should be much larger than omega
   
    private double[] window;
    private int omega;         // window size
    private int size;          // current # of elements
    private int startPos;
    
    public SlidingWindow (int omega) {
        this.omega = omega;
        this.window = new double[MAX_WINDOW_SIZE];
        Arrays.fill(this.window, 0);
        this.size = 0;
        this.startPos = 0;
    }

    public void push (double data) {
        // adds data to the tail of window
        if (size < omega) {
            window[size] = data;
            size++;
        }
        else {
            int writePos = startPos + size;

            // System.out.println("push, start_pos=" + startPos + ", writePos=" + writePos);

            if (writePos < MAX_WINDOW_SIZE) {
                // just shift the window, one data to the right
                window[writePos] = data;
                startPos++;
            }
            else {
                // not enough room in the window, copy the data to the beginning
                // src and dst should not be overlapped
                for (int i = 0; i < omega - 1; i++) {
                    window[i] = window[startPos + i + 1];
                }
                window[omega - 1] = data;
                startPos = 0;
            }
        }
    }

    public String toString() {
        String str = "";
        for (int i = 0; i < omega; i++) {
            if (i == omega - 1)
                str += window[startPos + i];
            else 
                str += window[startPos + i] + ", ";
        }

        return str;
    }

    public double at(int i) {
        // returns the element at index i
        return window[startPos + i];
    }

    public int getSize() {
        return size;
    }

    public int getOmega() {
        return omega;
    }

    public static void main(String args[]) {
        // test
        SlidingWindow win = new SlidingWindow(5);  // tested with MAX_WINDOW_SIZE=10

        for (int i = 1; i < 25; i++) {
            win.push(i);
            System.out.println("i=" + i + ", win=" + win + ", win.at(2)=" + win.at(2));
        }
    }
}
