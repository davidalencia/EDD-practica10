package mx.unam.ciencias.edd;

/**
 * Clase para métodos estáticos con dispersores de bytes.
 */
public class Dispersores {

    /* Constructor privado para evitar instanciación. */
    private Dispersores() {}

    /**
     * Función de dispersión XOR.
     * @param llave la llave a dispersar.
     * @return la dispersión de XOR de la llave.
     */
    public static int dispersaXOR(byte[] llave) {
        int r = 0;
        byte[][] bytes = reshape(llave);
        for (byte[] b: bytes)
          r ^= bigEndian(b);
        return r;
    }

    /**
     * Función de dispersión de Bob Jenkins.
     * @param llave la llave a dispersar.
     * @return la dispersión de Bob Jenkins de la llave.
     */
    public static int dispersaBJ(byte[] llave) {
        int a[] = {0x9E3779B9}, b[] = {0x9E3779B9}, c[] = {0xFFFFFFFF};
        byte[][] bytes = reshape(llave);
        int n = 0;
        while(n<bytes.length){
          if(n<bytes.length)
            a[0] += littleEndian(bytes[n++]);
          if(n<bytes.length)
            b[0] += littleEndian(bytes[n++]);
          if(n<bytes.length){
            int suma =  littleEndian(bytes[n++]);
            if(llave.length-n*4<0)
              suma = suma << 8;
            c[0] += suma;
          }

          if(n!=bytes.length)
            mezcla(a, b, c);
        }
        if(llave.length%12==0)
          mezcla(a,b,c);
        c[0] += llave.length;
        mezcla(a, b , c);
        return c[0];
    }
    /**
     * Función de dispersión Daniel J. Bernstein.
     * @param llave la llave a dispersar.
     * @return la dispersión de Daniel Bernstein de la llave.
     */
    public static int dispersaDJB(byte[] llave) {
      int h = 5381;
      for (byte k: llave) {
        h *= 33;
        h += (k & 0xFF);
      }
      return h;
    }

    private static byte[][] reshape(byte[] b){
      int y = b.length/4;
      y = (y*4==b.length)? y: y+1;
      byte[][] r = new byte[y][4];
      for (int alfa=0; alfa<y; alfa++)
        for (int beta=0; beta<4; beta++)
            r[alfa][beta] = (alfa*4+beta <b.length)? b[alfa*4+beta]: 0;
      return r;
    }
    private static void mezcla(int a[], int b[], int c[]){
      a[0] -= b[0];
      a[0] -= c[0];
      a[0] ^= (c[0] >>> 13);
      b[0] -= a[0];
      b[0] -= c[0];
      b[0] ^= a[0] << 8;
      c[0] -= a[0];
      c[0] -= b[0];
      c[0] ^= b[0] >>> 13;

      a[0] -= b[0];
      a[0] -= c[0];
      a[0] ^= c[0] >>> 12;
      b[0] -= a[0];
      b[0] -= c[0];
      b[0] ^= a[0] << 16;
      c[0] -= a[0];
      c[0] -= b[0];
      c[0] ^= b[0] >>> 5;

      a[0] -= b[0];
      a[0] -= c[0];
      a[0] ^= c[0] >>> 3;
      b[0] -= a[0];
      b[0] -= c[0];
      b[0] ^= a[0] << 10;
      c[0] -= a[0];
      c[0] -= b[0];
      c[0] ^= b[0] >>> 15;

    }
    private static int bigEndian(byte[] b){
      return ((b[0] & 0xFF) << 24) | ((b[1] & 0xFF) << 16) |
              ((b[2] & 0xFF) << 8) | ((b[3] & 0xFF));
    }
    private static int littleEndian(byte[] b){
      return ((b[3] & 0xFF) << 24) | ((b[2] & 0xFF) << 16) |
              ((b[1] & 0xFF) << 8) | ((b[0] & 0xFF));
    }
}
