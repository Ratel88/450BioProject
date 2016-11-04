package slider;


import java.awt.Dimension;
import java.awt.Rectangle;


/**
 * Open source code from the internet. Code available online at http://www2.gol.com/users/tame/swing/examples/JSliderExamples1.html
 * @version 1.0 09/08/99
 */
 //
 // MThumbSliderAdditionalUI <--> BasicMThumbSliderUI
 //                          <--> MetalMThumbSliderUI
 //                          <--> MotifMThumbSliderUI
 //
public interface MThumbSliderAdditional {

  public Rectangle getTrackRect();

  public Dimension getThumbSize();

  public int xPositionForValue(int value);

  public int yPositionForValue(int value);

}
