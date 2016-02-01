package org.ssh.ui.components.centersection.gamescene.shapes;

import javafx.scene.DepthTest;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 * @author Jeroen
 * @date 31-1-2016
 */
public class SurfaceChart extends MeshView {
    private int sizeY = 100;
    private int sizeX = 100;

    public SurfaceChart() {

        drawChart(createNoise(sizeX, sizeY));

        // mesh view
        setTranslateX(-0.5 * sizeX);
        setTranslateZ(-0.5 * sizeY);
        setCullFace(CullFace.NONE);
        setDrawMode(DrawMode.FILL);
        setDepthTest(DepthTest.ENABLE);
        setScaleX(6000/sizeX);
        setScaleZ(4000/sizeY);
    }

    public void drawChart(float[][] noiseArray){

        // mesh
        TriangleMesh mesh = new TriangleMesh();

        // create points for x/z
        float amplification = 400; // amplification of noise

        for (int x = 0; x < sizeX; x++) {
            for (int z = 0; z < sizeY; z++) {
                mesh.getPoints().addAll(x, noiseArray[x][z] * amplification, z);
            }
        }

        // texture
        for (float x = 0; x < sizeX - 1; x++) {
            for (float y = 0; y < sizeY - 1; y++) {

                float x0 = x / sizeX;
                float y0 = y / sizeX;
                float x1 = (x + 1) / sizeX;
                float y1 = (y + 1) / sizeX;

                mesh.getTexCoords().addAll( //
                        x0, y0, // 0, top-left
                        x0, y1, // 1, bottom-left
                        x1, y1, // 2, top-right
                        x1, y1 // 3, bottom-right
                );


            }
        }

        // faces
        for (int x = 0; x < sizeX - 1; x++) {
            for (int z = 0; z < sizeY - 1; z++) {

                int tl = x * sizeX + z; // top-left
                int bl = x * sizeY + z + 1; // bottom-left
                int tr = (x + 1) * sizeX + z; // top-right
                int br = (x + 1) * sizeY + z + 1; // bottom-right

                int offsetX = (x * (sizeX - 1) + z) * 8 / 2; // div 2 because we have u AND v in the list

                // working
                mesh.getFaces().addAll(bl, offsetX + 1, tl, offsetX + 0, tr, offsetX + 2);
                mesh.getFaces().addAll(tr, offsetX + 2, br, offsetX + 3, bl, offsetX + 1);

            }
        }

        // material
        Image diffuseMap = createImage(sizeX, sizeY, noiseArray);

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(diffuseMap);
        material.setSpecularColor(Color.WHITE);

        setMesh(mesh);
        setMaterial(material);
    }

    /**
     * Create texture for uv mapping
     * @param size
     * @param noise
     * @return
     */
    public Image createImage(double sizeX, double sizeY, float[][] noise) {

        int width = (int) sizeX;
        int height = (int) sizeY;

        WritableImage wr = new WritableImage(width, height);
        PixelWriter pw = wr.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                float value = noise[x][y];

                double gray = normalizeValue(value, -.5, .5, 0., 1.);

                gray = clamp(gray, 0, 1);

                Color color = Color.BLUE.interpolate(Color.RED, gray);

                pw.setColor(x, y, color);

            }
        }

        return wr;

    }


    /**
     * Create an array of the given size with values of perlin noise
     * @param size
     * @return
     */
    public float[][] createNoise( int sizeX, int sizeY) {
        long startTime = System.currentTimeMillis();

        float[][] noiseArray = new float[(int) sizeX][(int) sizeY];

        double rdmY = Math.random();
        double rdmX = Math.random();
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {

                double frequency = 10.0 / (double) sizeX;

                double noise = ImprovedNoise.noise(x * frequency * rdmX, y * frequency * rdmY, 0);

                noiseArray[x][y] = (float) noise;
            }
        }

        System.out.println("Total execution time: " + ( System.currentTimeMillis()-startTime) + "ms");
        return noiseArray;

    }

    public static double normalizeValue(double value, double min, double max, double newMin, double newMax) {

        return (value - min) * (newMax - newMin) / (max - min) + newMin;

    }

    public static double clamp(double value, double min, double max) {

        if (Double.compare(value, min) < 0)
            return min;

        if (Double.compare(value, max) > 0)
            return max;

        return value;
    }


    /**
     * Perlin noise generator
     *
     * // JAVA REFERENCE IMPLEMENTATION OF IMPROVED NOISE - COPYRIGHT 2002 KEN PERLIN.
     * // http://mrl.nyu.edu/~perlin/paper445.pdf
     * // http://mrl.nyu.edu/~perlin/noise/
     */
    public final static class ImprovedNoise {
        static public double noise(double x, double y, double z) {
            int X = (int)Math.floor(x) & 255,                  // FIND UNIT CUBE THAT
                    Y = (int)Math.floor(y) & 255,                  // CONTAINS POINT.
                    Z = (int)Math.floor(z) & 255;
            x -= Math.floor(x);                                // FIND RELATIVE X,Y,Z
            y -= Math.floor(y);                                // OF POINT IN CUBE.
            z -= Math.floor(z);
            double u = fade(x),                                // COMPUTE FADE CURVES
                    v = fade(y),                                // FOR EACH OF X,Y,Z.
                    w = fade(z);
            int A = p[X  ]+Y, AA = p[A]+Z, AB = p[A+1]+Z,      // HASH COORDINATES OF
                    B = p[X+1]+Y, BA = p[B]+Z, BB = p[B+1]+Z;      // THE 8 CUBE CORNERS,

            return lerp(w, lerp(v, lerp(u, grad(p[AA  ], x  , y  , z   ),  // AND ADD
                    grad(p[BA  ], x-1, y  , z   )), // BLENDED
                    lerp(u, grad(p[AB  ], x  , y-1, z   ),  // RESULTS
                            grad(p[BB  ], x-1, y-1, z   ))),// FROM  8
                    lerp(v, lerp(u, grad(p[AA+1], x  , y  , z-1 ),  // CORNERS
                            grad(p[BA+1], x-1, y  , z-1 )), // OF CUBE
                            lerp(u, grad(p[AB+1], x  , y-1, z-1 ),
                                    grad(p[BB+1], x-1, y-1, z-1 ))));
        }
        static double fade(double t) { return t * t * t * (t * (t * 6 - 15) + 10); }
        static double lerp(double t, double a, double b) { return a + t * (b - a); }
        static double grad(int hash, double x, double y, double z) {
            int h = hash & 15;                      // CONVERT LO 4 BITS OF HASH CODE
            double u = h<8 ? x : y,                 // INTO 12 GRADIENT DIRECTIONS.
                    v = h<4 ? y : h==12||h==14 ? x : z;
            return ((h&1) == 0 ? u : -u) + ((h&2) == 0 ? v : -v);
        }
        static final int p[] = new int[512], permutation[] = { 151,160,137,91,90,15,
                131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
                190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
                88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
                77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
                102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
                135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
                5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
                223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
                129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
                251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
                49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
                138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
        };
        static { for (int i=0; i < 256 ; i++) p[256+i] = p[i] = permutation[i]; }
    }


}
