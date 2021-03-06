/*******************************************************
* file: FinalProgramMinecraft
* authors: Scott Lowry, Daniel Lynch, Matt Capparelli
* class: CS 445 - Computer Graphics - Fall Section 01
*
* assignment: Final Program
* date last modified: 11/20/2015
*
* purpose: This program uses the LWJGL library to draw a window of 640x480
* in the center of the screen. The program will create an original scene in 
* Minecraft fashion.
* 
* Check Point 2 Requirements:
* The program has a window created that is 640x480 and centered on the screen. 
* It displays a 30x30 chunk of blocks with randomly generated block types: 
* grass, sand, dirt, water, stone, or bedrock.  The chunk is also displayed 
* with a randomly generated terrain . The program allows the user to manipulate 
* the camera with the mouse to give a first person appearance and be able to 
* navigate the environment using the input.Keyboard class with either the 
* w,a,s,d keys or the arrow keys to move around as well as the space bar to 
* move up and the left shift button to go down. The program allows the user to 
* hit the escape key to quit the application.
* 
*    Control the camera:
*       W/S/A/D And Up/Down/Left/Right (Arrow keys): Forward/Backward/Left/Right
*       Space Bar: Move Up
*       Left Shift: Move Down
*       Mouse: Looks around
*   The application was created using the guidelines from the slides from class.
*/
package finalprogramminecraft;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

//Main Class - contains the main method, creates frame
public class FinalProgramMinecraft {
    
    private FPCameraController fp; 
    private DisplayMode displayMode;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
        
    //main method
    public static void main(String[] args) {
        FinalProgramMinecraft basic = new FinalProgramMinecraft();
        basic.start();
    }
    
    //create window and start game loop
    public void start() {
        try {
            createWindow();
            initGL();
            fp = new FPCameraController(0f,0f,0f);
            fp.gameLoop();//render();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Define window properties
    private void createWindow() throws Exception{
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("Final Program Minecraft!");
        Display.create();
    }

    //Initialiaze GL properties
    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float) displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState (GL_TEXTURE_COORD_ARRAY);
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); //sets our light’s position
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);//sets our specular light
 //       glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);//sets our diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);//sets our ambient light
        glEnable(GL_LIGHTING);//enables our lighting
        glEnable(GL_LIGHT0);//enables light0
    }
    
    private void initLightArrays() {
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }
}


