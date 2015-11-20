package finalprogramminecraft;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
    static final int CHUNK_SIZE = 10;
    static final int CUBE_LENGTH = 2;
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    private Texture texture;
    
    private final int SIMPLEX_LARGEST_FEATURE = 5;
    private final float SIMPLEX_PERSISTANCE = 0.5f;
    private final int SIMPLEX_SEED = 7;
    
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
    
    public void rebuildMesh(float startX, float startY, float startZ) {
        
        SimplexNoise noise = new SimplexNoise(SIMPLEX_LARGEST_FEATURE, 
                SIMPLEX_PERSISTANCE, SIMPLEX_SEED);
        
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
                
                //int i = (int)(startX + x*(CHUNK_SIZE/640));
                //int j = (int)(startZ + z*(CHUNK_SIZE/480));
                int height = (int)(startY + (int)(100*noise.getNoise((int)x,(int)z) * CUBE_LENGTH));
                
                
                if (height>CHUNK_SIZE) {
                    height = CHUNK_SIZE;
                }
                
                for(float y = 0; y < height; y++){
                    VertexPositionData.put( createCube(
                            (float)(startX + x* CUBE_LENGTH),
                            (float)(y*CUBE_LENGTH+(int)(CHUNK_SIZE*.8)),
                            (float)(startZ + z *CUBE_LENGTH)));
                    
                    VertexColorData.put(createCubeVertexCol(getCubeColor(
                            Blocks[(int) x][(int) y][(int) z])));
                    VertexTextureData.put(createTexCube((float) 0, 
                            (float)0,Blocks[(int)(x)][(int) (y)][(int) (z)]));
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
    
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i %
            CubeColorArray.length];
        }
        return cubeColors;
    }

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

    private float[] getCubeColor(Block block) {
        return new float[] { 1, 1, 1 };
    }


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
                    
                    float rand = r.nextFloat();
                    
                    if(rand>0.830f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    }else if(rand>0.664f){
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
                }
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
    
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f/16)/1024f;
        switch (block.GetID()) {
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
                    x + offset*5, y + offset*7,
                    x + offset*6, y + offset*7,
                    x + offset*6, y + offset*8,
                    x + offset*5, y + offset*8,
                    // TOP!
                    x + offset*5, y + offset*7,
                    x + offset*6, y + offset*7,
                    x + offset*6, y + offset*8,
                    x + offset*5, y + offset*8,
                    // FRONT QUAD
                    x + offset*5, y + offset*7,
                    x + offset*6, y + offset*7,
                    x + offset*6, y + offset*8,
                    x + offset*5, y + offset*8,
                    // BACK QUAD
                    x + offset*5, y + offset*7,
                    x + offset*6, y + offset*7,
                    x + offset*6, y + offset*8,
                    x + offset*5, y + offset*8,
                    // LEFT QUAD
                    x + offset*5, y + offset*7,
                    x + offset*6, y + offset*7,
                    x + offset*6, y + offset*8,
                    x + offset*5, y + offset*8,
                    // RIGHT QUAD
                    x + offset*5, y + offset*7,
                    x + offset*6, y + offset*7,
                    x + offset*6, y + offset*8,
                    x + offset*5, y + offset*8};
            default: //bedrock block type
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*0, y + offset*1,
                    x + offset*1, y + offset*1,
                    x + offset*1, y + offset*2,
                    x + offset*0, y + offset*2,
                    // TOP!
                    x + offset*0, y + offset*1,
                    x + offset*1, y + offset*1,
                    x + offset*1, y + offset*2,
                    x + offset*0, y + offset*2,
                    // FRONT QUAD
                    x + offset*0, y + offset*1,
                    x + offset*1, y + offset*1,
                    x + offset*1, y + offset*2,
                    x + offset*0, y + offset*2,
                    // BACK QUAD
                    x + offset*0, y + offset*1,
                    x + offset*1, y + offset*1,
                    x + offset*1, y + offset*2,
                    x + offset*0, y + offset*2,
                    // LEFT QUAD
                    x + offset*0, y + offset*1,
                    x + offset*1, y + offset*1,
                    x + offset*1, y + offset*2,
                    x + offset*0, y + offset*2,
                    // RIGHT QUAD
                    x + offset*0, y + offset*1,
                    x + offset*1, y + offset*1,
                    x + offset*1, y + offset*2,
                    x + offset*0, y + offset*2};
        }
    }
}
