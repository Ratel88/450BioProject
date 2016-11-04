package slider;

import java.awt.Color;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.Icon;
import javax.swing.JSlider;


/**
  * Open source code from the internet. Code available online at http://www2.gol.com/users/tame/swing/examples/JSliderExamples1.html.
  * Update to place ranges on each knob so that they do not go past one another.
 */

public class MThumbSlider extends JSlider {


  protected int thumbNum;
  protected BoundedRangeModel[] sliderModels;
  protected Icon[] thumbRenderers;
  protected Color[] fillColors;
  protected Color trackFillColor;

  private static final String uiClassID = "MThumbSliderUI";

  public MThumbSlider(int n, int min, int max) {
    createThumbs(n, min, max);
    updateUI();

  }

  protected void createThumbs(int n, int min, int max) {
    thumbNum = n;
    sliderModels   = new BoundedRangeModel[n];
    thumbRenderers = new Icon[n];
    fillColors = new Color[n];
    for (int i=0;i<n;i++) {
      sliderModels[i] = new DefaultBoundedRangeModel(min, 0, min, max);

      thumbRenderers[i] = null;
      fillColors[i] = null;
    }
  }


  public void updateUI() {
    AssistantUIManager.setUIName(this);
    super.updateUI();


    // another way
    //
    updateLabelUIs();
    setUI(AssistantUIManager.createUI(this));
    //setUI(new BasicMThumbSliderUI(this));
    //setUI(new MetalMThumbSliderUI(this));
    //setUI(new MotifMThumbSliderUI(this));

  }

  public String getUIClassID() {
    return uiClassID;
  }




  public int getThumbNum() {
   return thumbNum;
  }

  public int getValueAt(int index) {
    return getModelAt(index).getValue();
  }

  public void setValueAt(int n, int index) {
    getModelAt(index).setValue(n);
    //createThumbs(n,index,index+1);
   // sliderModels[index] = new DefaultBoundedRangeModel(50, 0, 50, 101+1);

    //thumbRenderers[index] = null;
    //fillColors[index] = null;
    //updateUI();
  }

  public int getMinimum() {
    return getModelAt(0).getMinimum();
  }

  public int getMaximum() {
    return getModelAt(1).getMaximum();
  }

  public void setMiddleRange(){
    ((DefaultBoundedRangeModel)getModelAt(2)).setMinimum(getModelAt(0).getValue());
    ((DefaultBoundedRangeModel)getModelAt(2)).setMaximum(getModelAt(1).getValue());
    ((DefaultBoundedRangeModel)getModelAt(1)).setMinimum(getModelAt(2).getValue());
    ((DefaultBoundedRangeModel)getModelAt(0)).setMaximum(getModelAt(2).getValue());

  }

  public BoundedRangeModel getModelAt(int index) {
    return sliderModels[index];
  }

  public Icon getThumbRendererAt(int index) {
    return thumbRenderers[index];
  }

  public void setThumbRendererAt(Icon icon, int index) {
    thumbRenderers[index] = icon;
  }

  public Color getFillColorAt(int index) {
    return fillColors[index];
  }

  public void setFillColorAt(Color color, int index) {
    fillColors[index] = color;
  }

  public Color getTrackFillColor() {
    return trackFillColor;
  }

  public void setTrackFillColor(Color color) {
    trackFillColor = color;
  }
}


