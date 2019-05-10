package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * <p>Clase para árboles binarios ordenados. Los árboles son genéricos, pero
 * acotados a la interfaz {@link Comparable}.</p>
 *
 * <p>Un árbol instancia de esta clase siempre cumple que:</p>
 * <ul>
 *   <li>Cualquier elemento en el árbol es mayor o igual que todos sus
 *       descendientes por la izquierda.</li>
 *   <li>Cualquier elemento en el árbol es menor o igual que todos sus
 *       descendientes por la derecha.</li>
 * </ul>
 */
public class ArbolBinarioOrdenado<T extends Comparable<T>>
    extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Pila para recorrer los vértices en DFS in-order. */
        private Pila<Vertice> pila;

        /* Inicializa al iterador. */
        public Iterador() {
            pila = new Pila<Vertice>();
            Vertice v = raiz;
            while(v!=null){
              pila.mete(v);
              v=v.izquierdo;
            }
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return !pila.esVacia();
        }

        /* Regresa el siguiente elemento en orden DFS in-order. */
        @Override public T next() {
          Vertice v = pila.saca();
          if(v.derecho!=null){
            Vertice mueve = v.derecho;
            while(mueve!=null){
              pila.mete(mueve);
              mueve=mueve.izquierdo;
            }
          }
          return v.elemento;
        }
    }

    /**
     * El vértice del último elemento agegado. Este vértice sólo se puede
     * garantizar que existe <em>inmediatamente</em> después de haber agregado
     * un elemento al árbol. Si cualquier operación distinta a agregar sobre el
     * árbol se ejecuta después de haber agregado un elemento, el estado de esta
     * variable es indefinido.
     */
    protected Vertice ultimoAgregado;

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioOrdenado() { super(); }

    /**
     * Construye un árbol binario ordenado a partir de una colección. El árbol
     * binario ordenado tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario ordenado.
     */
    public ArbolBinarioOrdenado(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un nuevo elemento al árbol. El árbol conserva su orden in-order.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
      if(elemento==null)
        throw new IllegalArgumentException();
      elementos++;
      if(raiz==null){
        raiz=nuevoVertice(elemento);
        ultimoAgregado =raiz;
        return;
      }
      Vertice v = raiz;
      while(true){
        if(elemento.compareTo(v.elemento)>0)
          if(v.derecho!=null)
            v=v.derecho;
          else{
            v.derecho = nuevoVertice(elemento);
            v.derecho.padre = v;
            ultimoAgregado =v.derecho;
            return;
          }
        else
          if(v.izquierdo!=null)
            v=v.izquierdo;
          else{
            v.izquierdo = nuevoVertice(elemento);
            v.izquierdo.padre = v;
            ultimoAgregado =v.izquierdo;
            return ;
          }
      }

    }

    /**
     * Elimina un elemento. Si el elemento no está en el árbol, no hace nada; si
     * está varias veces, elimina el primero que encuentre (in-order). El árbol
     * conserva su orden in-order.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
       if(raiz==null || elemento==null)
        return;
      eliminaVertice(intercambiaEliminable(vertice(busca(elemento))));
      elementos--;
    }

    /**
     * Intercambia el elemento de un vértice con dos hijos distintos de
     * <code>null</code> con el elemento de un descendiente que tenga a lo más
     * un hijo.
     * @param vertice un vértice con dos hijos distintos de <code>null</code>.
     * @return el vértice descendiente con el que vértice recibido se
     *         intercambió. El vértice regresado tiene a lo más un hijo distinto
     *         de <code>null</code>.
     */
    protected Vertice intercambiaEliminable(Vertice vertice) {
      if(vertice==null)
        return null;
      if(vertice.izquierdo == null)
        return vertice;
      Vertice cambia = vertice.izquierdo;
      while(cambia.derecho!=null)
        cambia=cambia.derecho;
      vertice.elemento = cambia.elemento;
      return cambia;
    }

    /**
     * Elimina un vértice que a lo más tiene un hijo distinto de
     * <code>null</code> subiendo ese hijo (si existe).
     * @param vertice el vértice a eliminar; debe tener a lo más un hijo
     *                distinto de <code>null</code>.
     */
    protected void eliminaVertice(Vertice vertice) {
      if(raiz==vertice){
        raiz=vertice.derecho;
        if(vertice.derecho!=null)
          raiz.padre=null;
        return;
      }
      if(esHoja(vertice))
        eliminaHoja(vertice);
      else{
        Vertice hijo;
        if(vertice.izquierdo!=null)
          hijo=vertice.izquierdo;
        else
          hijo=vertice.derecho;
        hijo.padre=vertice.padre;
        if(vertice.padre!=null)
          if(vertice.padre.derecho==vertice)
            vertice.padre.derecho=hijo;
          else
            vertice.padre.izquierdo=hijo;
      }
    }
    private boolean esHoja(Vertice v){
      return v.izquierdo==null && v.derecho==null;
    }
    private void eliminaHoja(Vertice v){
      if(v.padre.izquierdo==v)
        v.padre.izquierdo=null;
      else
        v.padre.derecho=null;
    }

    /**
     * Busca un elemento en el árbol recorriéndolo in-order. Si lo encuentra,
     * regresa el vértice que lo contiene; si no, regresa <tt>null</tt>.
     * @param elemento el elemento a buscar.
     * @return un vértice que contiene al elemento buscado si lo
     *         encuentra; <tt>null</tt> en otro caso.
     */
    @Override public VerticeArbolBinario<T> busca(T elemento) {
      if(elemento==null)
        return null;
      Vertice v = raiz;
      while(v!=null && !v.elemento.equals(elemento))
        if(elemento.compareTo(v.elemento)>0)
          v=v.derecho;
        else
          v=v.izquierdo;
      return v;
    }

    /**
     * Regresa el vértice que contiene el último elemento agregado al
     * árbol. Este método sólo se puede garantizar que funcione
     * <em>inmediatamente</em> después de haber invocado al método {@link
     * agrega}. Si cualquier operación distinta a agregar sobre el árbol se
     * ejecuta después de haber agregado un elemento, el comportamiento de este
     * método es indefinido.
     * @return el vértice que contiene el último elemento agregado al árbol, si
     *         el método es invocado inmediatamente después de agregar un
     *         elemento al árbol.
     */
    public VerticeArbolBinario<T> getUltimoVerticeAgregado() {
        return ultimoAgregado;
    }

    /**
     * Gira el árbol a la derecha sobre el vértice recibido. Si el vértice no
     * tiene hijo izquierdo, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraDerecha(VerticeArbolBinario<T> vertice) {
      Vertice q = vertice(vertice);
    	if(raiz ==null || q==null || q.izquierdo==null)
    	  return;
    	Vertice p = q.izquierdo;
    	//padre
    	p.padre=q.padre;
    	if(q.padre!=null)
    	  if(q.padre.izquierdo==q)
    		  p.padre.izquierdo=p;
        else
    		  p.padre.derecho=p;
    	//q adopta
    	q.izquierdo = p.derecho;
    	if(q.izquierdo!=null)
    	  q.izquierdo.padre = q;
    	//p adopta
    	p.derecho = q;
    	q.padre = p;
    	if(q==raiz)
    	  raiz=p;

    }

    /**
     * Gira el árbol a la izquierda sobre el vértice recibido. Si el vértice no
     * tiene hijo derecho, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraIzquierda(VerticeArbolBinario<T> vertice) {
      Vertice p = vertice(vertice);
    	if(raiz ==null || p==null || p.derecho==null)
    	    return;
    	Vertice q = p.derecho;
    	//padre
    	q.padre=p.padre;
    	if(p.padre!=null)
    	  if(p.padre.izquierdo==p)
    		  q.padre.izquierdo=q;
        else
    		  q.padre.derecho=q;
    	//p adopta
    	p.derecho = q.izquierdo;
    	if(p.derecho!=null)
    	  p.derecho.padre = p;
    	//q adopta
    	q.izquierdo = p;
    	p.padre = q;
    	if(p==raiz)
    	  raiz=q;
    }
    /**
     * Realiza un recorrido DFS <em>pre-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPreOrder(AccionVerticeArbolBinario<T> accion) {
	dfsPreOrder(accion, raiz);
    }
    private void dfsPreOrder(AccionVerticeArbolBinario<T> accion, Vertice v){
	if(v==null)
	    return;
	accion.actua(v);
	dfsPreOrder(accion, v.izquierdo);
	dfsPreOrder(accion, v.derecho);
    }

    /**
     * Realiza un recorrido DFS <em>in-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsInOrder(AccionVerticeArbolBinario<T> accion) {
 	dfsInOrder(accion, raiz);
    }
    private void dfsInOrder(AccionVerticeArbolBinario<T> accion, Vertice v){
	if(v==null)
	    return;
	dfsInOrder(accion, v.izquierdo);
	accion.actua(v);
	dfsInOrder(accion, v.derecho);
    }

    /**
     * Realiza un recorrido DFS <em>post-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPostOrder(AccionVerticeArbolBinario<T> accion){
 	dfsPostOrder(accion, raiz);
    }
    private void dfsPostOrder(AccionVerticeArbolBinario<T> accion, Vertice v){
	if(v==null)
	    return;
	dfsPostOrder(accion, v.izquierdo);
	dfsPostOrder(accion, v.derecho);
	accion.actua(v);
    }
    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
