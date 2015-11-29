package finalprogramminecraft;

/*******************************************************
* file: Chunk.java
* authors: Scott Lowry, Daniel Lynch, Matt Capparelli
* class: CS 445 - Computer Graphics - Fall Section 01
*
* assignment: Final Program
* date last modified: 11/20/2015
*
* purpose: This is the Chunk object class, which is instantiated and represented 
* in the window.  It handles the randomization of the terrain and applying the 
* assigned textures to the appropriate blocks.
*/

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

//Chunk class stores block vertices in order to render them more efficiently
public class Chunk {
    
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    static final float persistanceMin = 0.18f; 
    static final float persistanceMax = 0.40f; 
    
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    private Texture texture;
    private static boolean topShelf = false;
    
    //render the chunk
    public void render(){
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER,VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER,VBOColorHandle);
        glColorPointer(3,GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2,GL_FLOAT,0,0L);

        glDrawArrays(GL_QUADS, 0,CHUNK_SIZE *CHUNK_SIZE*CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    //Primary method for generating terrain with simplex noise and drawing the chunk
    public void rebuildMesh(float startX, float startY, float startZ) {
        
        //Randomly generate a seed and persistence for the simplex noise
        Random random = new Random();
        
        //persistance is between 0 and 1, 1=rocky, 0=smooth
        float persistance = 0;
        while (persistance < persistanceMin) {
            persistance = (persistanceMax)*random.nextFloat();
        }
        int seed = (int)(50*random.nextFloat());
        
        SimplexNoise noise = new SimplexNoise(CHUNK_SIZE, persistance, seed);
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers(); 
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer(
                (CHUNK_SIZE * CHUNK_SIZE *CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer(
                (CHUNK_SIZE* CHUNK_SIZE *CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer(
                (CHUNK_SIZE*CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);
        
        for (float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                for (float y = 0; y < CHUNK_SIZE; y++) {    
                    
                    //generate height from simplex noise
                    int height = (int)(startY + Math.abs((int)(CHUNK_SIZE * noise.getNoise((int)x,(int)z))));
<<<<<<< HEAD
                    
                    if (y >= height) {
                        break;
                    }          
=======
                    if (y >= height) {
                        break;
                    }
                    
>>>>>>> adbce1464f35b74e0fd0ebee9d65c96142bb6b16
                    VertexPositionData.put( createCube(
                            (float)(startX + x* CUBE_LENGTH),
                            (float)(y*CUBE_LENGTH+(int)(CHUNK_SIZE*.8)),
                            (float)(startZ + z *CUBE_LENGTH)));
    //                        0f,0f,0f));
                    
                    VertexColorData.put(createCubeVertexCol(getCubeColor(
                            Blocks[(int) x][(int) y][(int) z])));
<<<<<<< HEAD
                    //Checks if it is bottom cube
                    if(y == 0){
                         VertexTextureData.put(createTexCube((float) 0, 
                            (float)0,Blocks[(int)(x)][(int)(y)][(int) (z)]));
                    }else if(y == height-1){ //If it is the top cube texture it with grass
                        topShelf = true;
                        VertexTextureData.put(createTexCube((float) 0, 
                            (float)0,Blocks[(int)(x)][(int)(y)][(int) (z)]));
                    }else{ //Not top or bottom cube                   
                    VertexTextureData.put(createTexCube((float) 0, 
                            (float)0,Blocks[(int)(x)][(int)(y)][(int) (z)]));
                    }
=======
                    
                    VertexTextureData.put(createTexCube((float) 0, 
                            (float)0,Blocks[(int)(x)][(int)(y)][(int) (z)]));
>>>>>>> adbce1464f35b74e0fd0ebee9d65c96142bb6b16
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER,VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexPositionData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER,VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexColorData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    //Return an array of the colors on each sie of the cube
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i %
            CubeColorArray.length];
        }
        return cubeColors;
    }

    //Create a cube at location x,y,z in the chunk by returning a set of vertexes
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
            // TOP QUAD
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            // BOTTOM QUAD
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            // FRONT QUAD
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            // BACK QUAD
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            // LEFT QUAD
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            // RIGHT QUAD
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z };
    }

    //Default cube color
    private float[] getCubeColor(Block block) {
        return new float[] { 1, 1, 1 };
    }

    //Chunk constructor which assigns block types to all blocks and then calls rebuildMesh() to draw terrain
    public Chunk(int startX, int startY, int startZ) {
        try{texture = TextureLoader.getTexture("PNG", 
                ResourceLoader.getResourceAsStream("terrain.png"));
        }
        catch(Exception e){
            System.out.print("ER-ROAR!");
        }
        r = new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {

                    //Block type is all psychadelic and shit because 
                    //rand is randomly generated each time the frame is updated...
                    //is it possible to set block type once only in the beginning?
                    //staticaly set block type?

<<<<<<< HEAD
                 //   float rand = r.nextFloat();
    if ( y == 0){
        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
    }else if(z == 29|| z == 0 || x == 29 || x == 0){              
    //  if(rand>0.830f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
    }else 
        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
        /*            }else if(rand>0.664f){
=======
                    float rand = r.nextFloat();

                    if(rand>0.830f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    }else if(rand>0.664f){
>>>>>>> adbce1464f35b74e0fd0ebee9d65c96142bb6b16
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                    }else if(rand>0.498f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    }else if(rand>0.332f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                    }else if(rand>0.166f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                    }else{
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                   }
        */         }
            }
        }
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
    
    //Assigns textures from terrain.png to the sides of each cube
    public static float[] createTexCube(float x, float y, Block block) {
<<<<<<< HEAD
        int tempBlockID;
=======
        
>>>>>>> adbce1464f35b74e0fd0ebee9d65c96142bb6b16
        float offset = (1024f/16)/1024f;
        // if the current block is the top block set texture to grass
        if(topShelf){
            tempBlockID = 0;
            topShelf = false;
        }else
            tempBlockID = block.GetID();
        
        switch (tempBlockID) {
            case 0: //Grass block type
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*3, y + offset*10,
                    x + offset*2, y + offset*10,
                    x + offset*2, y + offset*9,
                    x + offset*3, y + offset*9,
                    // TOP!
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    // FRONT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    // BACK QUAD
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    // LEFT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    // RIGHT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1};
            case 1: //sand block type
                return new float[] {
                     // BOTTOM QUAD(DOWN=+Y)
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    // TOP!
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    // FRONT QUAD
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    // BACK QUAD
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    // LEFT QUAD
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    // RIGHT QUAD
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2};
            case 2: //water block type
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*2, y + offset*11,
                    x + offset*3, y + offset*11,
                    x + offset*3, y + offset*12,
                    x + offset*2, y + offset*12,
                    // TOP!
                    x + offset*2, y + offset*11,
                    x + offset*3, y + offset*11,
                    x + offset*3, y + offset*12,
                    x + offset*2, y + offset*12,
                    // FRONT QUAD
                    x + offset*2, y + offset*11,
                    x + offset*3, y + offset*11,
                    x + offset*3, y + offset*12,
                    x + offset*2, y + offset*12,
                    // BACK QUAD
                    x + offset*2, y + offset*11,
                    x + offset*3, y + offset*11,
                    x + offset*3, y + offset*12,
                    x + offset*2, y + offset*12,
                    // LEFT QUAD
                    x + offset*2, y + offset*11,
                    x + offset*3, y + offset*11,
                    x + offset*3, y + offset*12,
                    x + offset*2, y + offset*12,
                    // RIGHT QUAD
                    x + offset*2, y + offset*11,
                    x + offset*3, y + offset*11,
                    x + offset*3, y + offset*12,
                    x + offset*2, y + offset*12};
            case 3: //dirt block type
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*1, y + offset*10,
                    x + offset*2, y + offset*10,
                    x + offset*2, y + offset*11,
                    x + offset*1, y + offset*11,
                    // TOP!
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    // FRONT QUAD
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    // BACK QUAD
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    // LEFT QUAD
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    // RIGHT QUAD
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0};
            case 4: //stone block type
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*5, y + offset*6,
                    x + offset*6, y + offset*6,
                    x + offset*6, y + offset*7,
                    x + offset*5, y + offset*7,
                    // TOP!
                    x + offset*5, y + offset*6,
                    x + offset*6, y + offset*6,
                    x + offset*6, y + offset*7,
                    x + offset*5, y + offset*7,
                    // FRONT QUAD
                    x + offset*5, y + offset*6,
                    x + offset*6, y + offset*6,
                    x + offset*6, y + offset*7,
                    x + offset*5, y + offset*7,
                    // BACK QUAD
                    x + offset*5, y + offset*6,
                    x + offset*6, y + offset*6,
                    x + offset*6, y + offset*7,
                    x + offset*5, y + offset*7,
                    // LEFT QUAD
                    x + offset*5, y + offset*6,
                    x + offset*6, y + offset*6,
                    x + offset*6, y + offset*7,
                    x + offset*5, y + offset*7,
                    // RIGHT QUAD
                    x + offset*5, y + offset*6,
                    x + offset*6, y + offset*6,
                    x + offset*6, y + offset*7,
                    x + offset*5, y + offset*7};
            default: //bedrock block type
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                    // TOP!
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                    // FRONT QUAD
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                    // BACK QUAD
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                    // LEFT QUAD
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                    // RIGHT QUAD
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2};
        }
    }
}
