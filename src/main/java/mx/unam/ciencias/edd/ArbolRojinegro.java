package mx.unam.ciencias.edd;

/**
 * Clase para árboles rojinegros. Un árbol rojinegro cumple las siguientes
 * propiedades:
 *
 * <ol>
 *  <li>Todos los vértices son NEGROS o ROJOS.</li>
 *  <li>La raíz es NEGRA.</li>
 *  <li>Todas las hojas (<tt>null</tt>) son NEGRAS (al igual que la raíz).</li>
 *  <li>Un vértice ROJO siempre tiene dos hijos NEGROS.</li>
 *  <li>Todo camino de un vértice a alguna de sus hojas descendientes tiene el
 *      mismo número de vértices NEGROS.</li>
 * </ol>
 *
 * Los árboles rojinegros se autobalancean.
 */
public class ArbolRojinegro<T extends Comparable<T>>
    extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class VerticeRojinegro extends Vertice {

        /** El color del vértice. */
        public Color color;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public VerticeRojinegro(T elemento) {
          super(elemento);
          color = Color.ROJO;
        }

        /**
         * Regresa una representación en cadena del vértice rojinegro.
         * @return una representación en cadena del vértice rojinegro.
         */
        public String toString() {
            String c = color.equals(Color.NEGRO)? "N":"R";
	    return c+"{"+elemento+"}";
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>.
         * @param objeto el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeRojinegro}, su elemento es igual al elemento de
         *         éste vértice, los descendientes de ambos son recursivamente
         *         iguales, y los colores son iguales; <code>false</code> en
         *         otro caso.
         */
        @Override public boolean equals(Object objeto) {
            return super.equals(objeto);
        }


        private void paintItBlack(){
          color =Color.NEGRO;
        }
        private void paintItRed(){
          color =Color.ROJO;
        }

        private boolean esIzquierdo(){
          if(padre==null)
            return false;
          return padre.izquierdo==this;
        }
        private boolean esDerecho(){
          if(padre==null)
            return false;
          return padre.derecho==this;
        }
        private boolean esCruzado(){
          boolean hIzq = esIzquierdo()? true: false;
          boolean pIzq = rnPadre().esIzquierdo()? true: false;
          return (hIzq||pIzq) && !(hIzq&&pIzq);
        }

        private VerticeRojinegro rnIzquierdo(){
            return (VerticeRojinegro) izquierdo;
        }
        private VerticeRojinegro rnDerecho(){
            return (VerticeRojinegro) derecho;
        }
        private VerticeRojinegro rnPadre(){
            return (VerticeRojinegro) padre;
        }

        private VerticeRojinegro hermano(){
            if(padre != null)
              if(esIzquierdo())
                return (VerticeRojinegro) padre.derecho;
              else
                return (VerticeRojinegro) padre.izquierdo;
            return null;
        }
        private VerticeRojinegro abuelo(){
            if(padre != null)
              return (VerticeRojinegro) padre.padre;
            return null;
        }
        private VerticeRojinegro tio(){
          if(padre!=null && padre.padre!=null){
            if(rnPadre().esIzquierdo())
              return (VerticeRojinegro) padre.padre.derecho;
            return (VerticeRojinegro) padre.padre.izquierdo;
          }
          return null;
        }
        private VerticeRojinegro sobrinoIzq(){
          VerticeRojinegro h = hermano();
          if(h!=null)
            return h.rnIzquierdo();
          return null;
        }
        private VerticeRojinegro sobrinoDer(){
          VerticeRojinegro h = hermano();
          if(h!=null)
            return h.rnDerecho();
          return null;
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinarioOrdenado}.
     */
    public ArbolRojinegro() { super(); }

    /**
     * Construye un árbol rojinegro a partir de una colección. El árbol
     * rojinegro tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        rojinegro.
     */
    public ArbolRojinegro(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link
     * VerticeRojinegro}.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice rojinegro con el elemento recibido dentro del mismo.
     */
    @Override protected Vertice nuevoVertice(T elemento) {
        return new VerticeRojinegro(elemento);
    }

    /**
     * Regresa el color del vértice rojinegro.
     * @param vertice el vértice del que queremos el color.
     * @return el color del vértice rojinegro.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         VerticeRojinegro}.
     */
    public Color getColor(VerticeArbolBinario<T> vertice) {
        try{
          VerticeRojinegro v= (VerticeRojinegro) vertice;
          return v.color;
        }
        catch (Exception e) {
          return Color.NINGUNO;
        }
    }

    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método {@link
     * ArbolBinarioOrdenado#agrega}, y después balancea el árbol recoloreando
     * vértices y girando el árbol como sea necesario.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        super.agrega(elemento);
        rebalancea((VerticeRojinegro) ultimoAgregado);
    }
    private void rebalancea(VerticeRojinegro v){
      if(!v.hayPadre())
        v.paintItBlack();
      else if(esNegro(v.rnPadre()))
        return;
      else if(esRojo(v.tio())){
        v.tio().paintItBlack();
        v.rnPadre().paintItBlack();
        v.abuelo().paintItRed();
        rebalancea(v.abuelo());
      }
      else if(v.esCruzado()){
        VerticeRojinegro p = v.rnPadre();
        giraEnContra(v, v.padre);
        rebalancea(p);
      }
      else{
        v.rnPadre().paintItBlack();
        v.abuelo().paintItRed();
        giraEnContra(v, v.abuelo());
      }
    }
    private void giraEnContra(VerticeRojinegro direccion, Vertice pibote){
      if(direccion.esIzquierdo())
        super.giraDerecha(pibote);
      else
        super.giraIzquierda(pibote);
    }
    private void gira(VerticeRojinegro direccion, Vertice pibote){
      if(direccion.esDerecho())
        super.giraDerecha(pibote);
      else
        super.giraIzquierda(pibote);
    }

    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene
     * el elemento, y recolorea y gira el árbol como sea necesario para
     * rebalancearlo.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento) {
        VerticeRojinegro v =
            (VerticeRojinegro) intercambiaEliminable(vertice(busca(elemento)));
        if(v==null)
          return;
        Boolean fantasma=false;
        VerticeRojinegro rebalancea=null;
        if(v.izquierdo!=null)
          rebalancea = v.rnIzquierdo();
        else if(v.derecho!=null)
          rebalancea = v.rnDerecho();
        else{
          v.izquierdo = rebalancea = new VerticeRojinegro(null);
          v.rnIzquierdo().paintItBlack();
          fantasma = true;
        }

        reconecta(v, rebalancea);
        if(faltaRebalancear(v, rebalancea))
          rebalanceaElimina(rebalancea);

        if(fantasma)
          reconecta(rebalancea, null);

        elementos--;
    }
    private boolean faltaRebalancear(VerticeRojinegro sale, VerticeRojinegro entra){
      if(esRojo(entra))
        entra.paintItBlack();
      else if(esNegro(sale))
        return true;
      return false;
    }
    private void reconecta(Vertice sale, Vertice entra){
      if(sale.padre==null){
        raiz=entra;
        if(raiz!=null)
          raiz.padre=null;
      }
      else{
        if(sale.padre.izquierdo==sale)
          sale.padre.izquierdo=entra;
        else
          sale.padre.derecho=entra;
        if(entra!=null)
          entra.padre = sale.padre;
      }
    }
    private void rebalanceaElimina(VerticeRojinegro v){
      //caso 1
      if(!v.hayPadre())
        return;
      //caso 2
      else if(esRojo(v.hermano())){
        v.rnPadre().paintItRed();
        v.hermano().paintItBlack();
        gira(v, v.rnPadre());
        rebalanceaElimina(v);
      }
      //caso 3
      else if(esNegro(v.rnPadre())&&
              esNegro(v.sobrinoIzq())&&
              esNegro(v.sobrinoDer())){
        v.hermano().paintItRed();
        rebalanceaElimina(v.rnPadre());
      }
      //caso 4
      else if(esRojo(v.rnPadre())&&
              esNegro(v.sobrinoIzq())&&
              esNegro(v.sobrinoDer())){
        v.hermano().paintItRed();
        v.rnPadre().paintItBlack();
      }
      //caso 5
      else if((esRojo(v.sobrinoIzq())&&v.sobrinoIzq().esCruzado())
            ||(esRojo(v.sobrinoDer())&&v.sobrinoDer().esCruzado())){
        v.hermano().paintItRed();
        if(esRojo(v.sobrinoIzq())&&v.sobrinoIzq().esCruzado())
          v.sobrinoIzq().paintItBlack();
        else
          v.sobrinoDer().paintItBlack();
        giraEnContra(v, v.hermano());
        rebalanceaElimina(v);

      }
      //caso 6
      else{
        v.hermano().color = v.rnPadre().color;
        v.rnPadre().paintItBlack();
        if(v.esDerecho())
          v.sobrinoIzq().paintItBlack();
        else
          v.sobrinoDer().paintItBlack();
        gira(v, v.padre);
      }

    }
    private void caso6(VerticeRojinegro v){
      v.hermano().color = v.rnPadre().color;
      v.rnPadre().paintItBlack();
      if(v.esDerecho())
        v.sobrinoIzq().paintItBlack();
      else
        v.sobrinoDer().paintItBlack();
      gira(v, v.padre);
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la izquierda por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la izquierda " +
                                                "por el usuario.");
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la derecha por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la derecha " +
                                                "por el usuario.");
    }

    private boolean esNegro(VerticeRojinegro v){
      if(v==null)
        return true;
      return v.color.equals(Color.NEGRO);
    }
    private boolean esRojo(VerticeRojinegro v){
      if(v==null)
        return false;
      return v.color.equals(Color.ROJO);
    }
}
