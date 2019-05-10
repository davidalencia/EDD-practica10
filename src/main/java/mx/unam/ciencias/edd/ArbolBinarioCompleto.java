package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * <p>Clase para árboles binarios completos.</p>
 *
 * <p>Un árbol binario completo agrega y elimina elementos de tal forma que el
 * árbol siempre es lo más cercano posible a estar lleno.</p>
 */
public class ArbolBinarioCompleto<T> extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Cola para recorrer los vértices en BFS. */
        private Cola<Vertice> cola;

        /* Inicializa al iterador. */
        public Iterador() {
          cola = new Cola<Vertice>();
          if(raiz!=null)
            cola.mete(raiz);
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return !cola.esVacia();
        }

        /* Regresa el siguiente elemento en orden BFS. */
        @Override public T next() {
            Vertice v = cola.saca();
            avanzaCola(cola, v);
            return v.elemento;
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioCompleto() { super(); }

    /**
     * Construye un árbol binario completo a partir de una colección. El árbol
     * binario completo tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario completo.
     */
    public ArbolBinarioCompleto(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un elemento al árbol binario completo. El nuevo elemento se coloca
     * a la derecha del último nivel, o a la izquierda de un nuevo nivel.
     * @param elemento el elemento a agregar al árbol.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento) {
      if(elemento==null)
        throw new IllegalArgumentException();
      if(raiz==null)
        raiz = nuevoVertice(elemento);
      int h = altura(), n = elementos;
      Vertice v = raiz;
      while(n>2){
        boolean p = pow2(h+1)==n+1,  q = n+1-pow2(h)< pow2(h-1);
        if(p||q){//lleno o izq
          v=v.izquierdo;
          n= p? n-pow2(h):n-pow2(h-1); //lleno? lleno: izq
        }
        else{//derecho
          v=v.derecho;
          n=n-pow2(h);
        }
        h--;
      }
      Vertice nuevo = nuevoVertice(elemento);
      if(n==1)
        v.izquierdo = nuevo;
      if(n==2)
        v.derecho = nuevo;
      nuevo.padre = v;
      elementos++;
    }
    private int pow2(int h){
      return (int) Math.floor(Math.pow(2, h));
    }

    /**
     * Elimina un elemento del árbol. El elemento a eliminar cambia lugares con
     * el último elemento del árbol al recorrerlo por BFS, y entonces es
     * eliminado.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
      if(raiz==null  || elemento==null)
        return;
      Vertice v= (Vertice) busca(elemento);
      if(v==null)
        return ;
      if(elementos==1){
    	  raiz=null;
    	  elementos--;
    	  return;
      }
      Cola<Vertice> cola = new Cola<>();
      Vertice cambia = raiz;
      cola.mete(raiz);
      while(!cola.esVacia()){
    	  cambia = cola.saca();
    	  avanzaCola(cola, cambia);
      }
      v.elemento =cambia.elemento;
      eliminaHoja(cambia);
      elementos--;
    }
    private void eliminaHoja(Vertice v){
      if(v.padre.izquierdo==v)
        v.padre.izquierdo=null;
      else
        v.padre.derecho=null;
    }

    /**
     * Regresa la altura
del árbol. La altura de un árbol binario completo
     * siempre es ⌊log<sub>2</sub><em>n</em>⌋.
     * @return la altura del árbol.
     */
    @Override public int altura() {
      if(raiz==null)
        return -1;
      return (int) Math.floor(Math.log(elementos)/Math.log(2));
    }

    /**
     * Realiza un recorrido BFS en el árbol, ejecutando la acción recibida en
     * cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void bfs(AccionVerticeArbolBinario<T> accion) {
      if(raiz==null)
	     return;
    	Cola<Vertice> cola = new Cola<>();
    	cola.mete(raiz);
    	while(!cola.esVacia()){
    	    Vertice v = cola.saca();
    	    avanzaCola(cola, v);
    	    accion.actua(v);
	    }

    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden BFS.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    private void avanzaCola(Cola<Vertice> c, Vertice v){
      if(v.izquierdo!=null)
        c.mete(v.izquierdo);
      if(v.derecho!=null)
        c.mete(v.derecho);
    }
}
