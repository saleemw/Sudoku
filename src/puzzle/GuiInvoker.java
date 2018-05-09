package puzzle;

public class GuiInvoker {

    public static void main(String[] args) {

        final Runnable rn = () -> {
            try {
                GuiComponent.displayFrame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        javax.swing.SwingUtilities.invokeLater(rn);
    }
}
