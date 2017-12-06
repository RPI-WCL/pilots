package pilots.runtime.estimator.errsig;

import java.util.Arrays;

public class SlidingWindow {
    private static final int MAX_WINDOW_SIZE = 1024; // should be much larger than omega
   
    private double[] window_;
    private int omega_;         // window size
    private int size_;          // current # of elements
    private int start_pos_;
    
    public SlidingWindow (int omega ) {
        omega_ = omega;
        window_ = new double[MAX_WINDOW_SIZE];
        Arrays.fill( window_, 0 );
        size_ = 0;
        start_pos_ = 0;
    }

    public void push ( double data ) {
        // adds data to the tail of window
        if (size_ < omega_) {
            window_[size_] = data;
            size_++;
        }
        else {
            int write_pos = start_pos_ + size_;

            // System.out.println( "push, start_pos=" + start_pos_ + ", write_pos=" + write_pos );

            if (write_pos < MAX_WINDOW_SIZE) {
                // just shift the window, one data to the right
                window_[write_pos] = data;
                start_pos_++;
            }
            else {
                // not enough room in the window, copy the data to the beginning
                // src and dst should not be overlapped
                for (int i = 0; i < omega_ - 1; i++) {
                    window_[i] = window_[start_pos_ + i + 1];
                }
                window_[omega_ - 1] = data;
                start_pos_ = 0;
            }
        }
    }

    public String toString() {
        String str = "";
        for (int i = 0; i < omega_; i++) {
            if (i == omega_ - 1)
                str += window_[start_pos_ + i];
            else 
                str += window_[start_pos_ + i] + ", ";
        }

        return str;
    }

    public double at( int i ) {
        // returns the element at index i
        return window_[start_pos_ + i];
    }

    public int getSize() {
        return size_;
    }

    public int getOmega() {
        return omega_;
    }

    public static void main( String args[] ) {
        // test
        SlidingWindow win = new SlidingWindow( 5 );  // tested with MAX_WINDOW_SIZE=10

        for (int i = 1; i < 25; i++) {
            win.push( i );
            System.out.println( "i=" + i + ", win=" + win + ", win.at(2)=" + win.at(2) );
        }
    }
}
