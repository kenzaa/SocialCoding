package jogamp.routine.jogl.fixedfunctionpipeline;

/**
 **   __ __|_  ___________________________________________________________________________  ___|__ __
 **  //    /\                                           _                                  /\    \\  
 ** //____/  \__     __ _____ _____ _____ _____ _____  | |     __ _____ _____ __        __/  \____\\ 
 **  \    \  / /  __|  |     |   __|  _  |     |  _  | | |  __|  |     |   __|  |      /\ \  /    /  
 **   \____\/_/  |  |  |  |  |  |  |     | | | |   __| | | |  |  |  |  |  |  |  |__   "  \_\/____/   
 **  /\    \     |_____|_____|_____|__|__|_|_|_|__|    | | |_____|_____|_____|_____|  _  /    /\     
 ** /  \____\                       http://jogamp.org  |_|                              /____/  \    
 ** \  /   "' _________________________________________________________________________ `"   \  /    
 **  \/____.                                                                             .____\/     
 **
 ** Simple GL2-Profile demonstration using wavefront object loading, display-lists, diffuse
 ** texturing, basic materials and lighting. Also uses OffsetTableUtils to precalculate some
 ** nice cosinus based object movement. For an impression how this routine looks like see here:
 ** http://www.youtube.com/watch?v=LHfDiUBNim8
 **
 **/

import framework.base.*;
import framework.util.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.gl2.*;
import com.jogamp.opengl.util.texture.*;
import static javax.media.opengl.GL2.*;

public class GL2_UVUnwrap extends BaseRoutineAdapter implements BaseRoutineInterface {

    private int mDisplayListID;
    private float[] mOffsetSinTable;
    private static final int OFFSETSINTABLE_SIZE = 2700;
    private Texture mTexture;

    public void initRoutine(GL2 inGL,GLU inGLU,GLUT inGLUT) {
        mOffsetSinTable = OffsetTableUtils.cosaque_SinglePrecision(OFFSETSINTABLE_SIZE,360,true,OffsetTableUtils.TRIGONOMETRIC_FUNCTION.SIN);
        mTexture = TextureUtils.loadImageAsTexture_UNMODIFIED(inGL,"/binaries/textures/TextureBaking_Normals.dds");
        mTexture.enable(inGL);
        mTexture.bind(inGL);
        TextureUtils.preferAnisotropicFilteringOnTextureTarget(inGL,mTexture);
        inGL.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);	
        inGL.glLightModeli(GL_LIGHT_MODEL_COLOR_CONTROL,GL_SEPARATE_SPECULAR_COLOR);
        inGL.glEnable(GL_COLOR_SUM);
        inGL.glShadeModel(GL_SMOOTH);
        inGL.glCullFace(GL_BACK);
        inGL.glFrontFace(GL_CCW);
        inGL.glEnable(GL_CULL_FACE);
        inGL.glEnable(GL_DEPTH_TEST);	
        inGL.glLightModelfv(GL_LIGHT_MODEL_AMBIENT, DirectBufferUtils.createDirectFloatBuffer(new float[]{0.0f,0.0f,0.0f,0.0f}));
        inGL.glLightfv(GL_LIGHT0, GL_AMBIENT, DirectBufferUtils.createDirectFloatBuffer(new float[]{0.25f,0.25f,0.25f,1.0f}));
        inGL.glLightfv(GL_LIGHT0, GL_DIFFUSE, DirectBufferUtils.createDirectFloatBuffer(new float[]{1.0f,1.0f,1.0f,1.0f}));
        inGL.glLightfv(GL_LIGHT0, GL_SPECULAR, DirectBufferUtils.createDirectFloatBuffer(new float[]{1.0f,1.0f,1.0f,1.0f}));
        inGL.glLightfv(GL_LIGHT0, GL_POSITION, DirectBufferUtils.createDirectFloatBuffer(new float[]{-100.0f,100.0f,50.0f,1.0f}));
        inGL.glEnable(GL_LIGHTING);
        inGL.glEnable(GL_LIGHT0);
        inGL.glMaterialfv(GL_FRONT, GL_SPECULAR, DirectBufferUtils.createDirectFloatBuffer(new float[]{1.0f,1.0f,1.0f,1.0f}));
        inGL.glMateriali(GL_FRONT, GL_SHININESS, 12);		
        mDisplayListID = WavefrontObjectLoader_DisplayList.loadWavefrontObjectAsDisplayList(inGL,"/binaries/geometry/TextureBaking_Normals.wobj.zip");
    }

    public void mainLoop(int inFrameNumber,GL2 inGL,GLU inGLU,GLUT inGLUT) {	
        inGL.glPushMatrix();
            inGL.glLoadIdentity();
            inGL.glTranslatef(0.0f, 0.0f, -2.75f);
            inGL.glRotatef(mOffsetSinTable[(inFrameNumber)%OFFSETSINTABLE_SIZE], 0.25f, 1.0f, 0.5f);
            inGL.glRotatef(mOffsetSinTable[(int)(inFrameNumber*1.5f)%OFFSETSINTABLE_SIZE], 0.75f, 0.3f, 0.1f);
            inGL.glCallList(mDisplayListID+0);
        inGL.glPopMatrix();
    }

    public void cleanupRoutine(GL2 inGL,GLU inGLU,GLUT inGLUT) {
        inGL.glDeleteLists(mDisplayListID,1);
        mTexture.destroy(inGL);
        inGL.glFlush();
    }

}
