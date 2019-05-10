package mx.unam.ciencias.edd;

import java.util.NoSuchElementException;

/**
 * <p>Clase abstracta para árboles binarios genéricos.</p>
 *
 * <p>La clase proporciona las operaciones básicas para árboles binarios, pero
 * deja la implementación de varias en manos de las subclases concretas.</p>
 */
public abstract class ArbolBinario<T> implements Coleccion<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class Vertice implements VerticeArbolBinario<T> {

        /** El elemento del vértice. */
        public T elemento;
        /** El padre del vértice. */
        public Vertice padre;
        /** El izquierdo del vértice. */
        public Vertice izquierdo;
        /** El derecho del vértice. */
        public Vertice derecho;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public Vertice(T elemento) {
            this.elemento=elemento;
        }

        /**
         * Nos dice si el vértice tiene un padre.
         * @return <tt>true</tt> si el vértice tiene padre,
         *         <tt>false</tt> en otro caso.
         */
        @Override public boolean hayPadre() {
          return padre!=null;
        }

        /**
         * Nos dice si el vértice tiene un izquierdo.
         * @return <tt>true</tt> si el vértice tiene izquierdo,
         *         <tt>false</tt> en otro caso.
         */
        @Override public boolean hayIzquierdo() {
          return izquierdo!=null;
        }

        /**
         * Nos dice si el vértice tiene un derecho.
         * @return <tt>true</tt> si el vértice tiene derecho,
         *         <tt>false</tt> en otro caso.
         */
        @Override public boolean hayDerecho() {
          return derecho!=null;
        }

        /**
         * Regresa el padre del vértice.
         * @return el padre del vértice.
         * @throws NoSuchElementException si el vértice no tiene padre.
         */
        @Override public VerticeArbolBinario<T> padre() {
            if(padre==null)
              throw new NoSuchElementException("There is no father");
            return padre;
        }

        /**
         * Regresa el izquierdo del vértice.
         * @return el izquierdo del vértice.
         * @throws NoSuchElementException si el vértice no tiene izquierdo.
         */
        @Override public VerticeArbolBinario<T> izquierdo() {
          if(izquierdo==null)
            throw new NoSuchElementException("There is no left");
          return izquierdo;
        }

        /**
         * Regresa el derecho del vértice.
         * @return el derecho del vértice.
         * @throws NoSuchElementException si el vértice no tiene derecho.
         */
        @Override public VerticeArbolBinario<T> derecho() {
          if(derecho==null)
            throw new NoSuchElementException("There is no right, we need rights Vivre la revolucion");
          return derecho;
        }

        /**
         * Regresa la altura del vértice.
         * @return la altura del vértice.
         */
        @Override public int altura() {
          int hIzq=h(izquierdo);
          int hDer=h(derecho);
          return (hIzq>hDer)? hIzq: hDer;
        }
        private int h(Vertice v){
          if(v==null)
            return 0;
          int hIzq=h(v.izquierdo)+1;
          int hDer=h(v.derecho)+1;
          return (hIzq>hDer)? hIzq: hDer;
        }

        /**
         * Regresa la profundidad del vértice.
         * @return la profundidad del vértice.
         */
        @Override public int profundidad() {
          int p=0;
          Vertice v= this.padre;
          while(v!=null){
            v=v.padre;
            p++;
          }
          return p;
        }

        /**
         * Regresa el elemento al que apunta el vértice.
         * @return el elemento al que apunta el vértice.
         */
        @Override public T get() {
          return elemento;
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>. Las clases que extiendan {@link Vertice} deben
         * sobrecargar el método {@link Vertice#equals}.
         * @param objeto el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link Vertice}, su elemento es igual al elemento de éste
         *         vértice, y los descendientes de ambos son recursivamente
         *         iguales; <code>false</code> en otro caso.
         */
        @Override public boolean equals(Object objeto) {
            if (objeto == null || getClass() != objeto.getClass())
                return false;
            return equals(this,  (ArbolBinario.Vertice) objeto);
        }
        private boolean equals(ArbolBinario.Vertice v, ArbolBinario.Vertice otroV){
          if(v==null && otroV==null)
            return true;
          else if(v==null || otroV==null)
            return false;
          return v.elemento.equals(otroV.elemento)&&
            equals(v.izquierdo, otroV.izquierdo)&&
            equals(v.derecho, otroV.derecho);
        }

        /**
         * Regresa una representación en cadena del vértice.
         * @return una representación en cadena del vértice.
         */
        public String toString() {
            return elemento.toString();
        }
    }

    /** La raíz del árbol. */
    protected Vertice raiz;
    /** El número de elementos */
    protected int elementos;

    /**
     * Constructor sin parámetros. Tenemos que definirlo para no perderlo.
     */
    public ArbolBinario() {}

    /**
     * Construye un árbol binario a partir de una colección. El árbol binario
     * tendrá los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario.
     */
    public ArbolBinario(Coleccion<T> coleccion) {
        for (T t: coleccion)
          agrega(t);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link Vertice}. Para
     * crear vértices se debe utilizar este método en lugar del operador
     * <code>new</code>, para que las clases herederas de ésta puedan
     * sobrecargarlo y permitir que cada estructura de árbol binario utilice
     * distintos tipos de vértices.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice con el elemento recibido dentro del mismo.
     */
    protected Vertice nuevoVertice(T elemento) {
        return new Vertice(elemento);
    }

    /**
     * Regresa la altura del árbol. La altura de un árbol es la altura de su
     * raíz.
     * @return la altura del árbol.
     */
    public int altura() {
        if(raiz==null)
          return -1;
        return raiz.altura();
    }

    /**
     * Regresa el número de elementos que se han agregado al árbol.
     * @return el número de elementos en el árbol.
     */
    @Override public int getElementos() {
      return elementos;
    }

    /**
     * Nos dice si un elemento está en el árbol binario.
     * @param elemento el elemento que queremos comprobar si está en el árbol.
     * @return <code>true</code> si el elemento está en el árbol;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
      if(elemento==null || raiz==null)
        return false;
      Cola<Vertice> c = new Cola<>();
      c.mete(raiz);
      while(!c.esVacia()){
        Vertice v = c.saca();
        if(elemento.equals(v.elemento))
          return true;
        if(v.izquierdo!=null)
          c.mete(v.izquierdo);
        if(v.derecho!=null)
          c.mete(v.derecho);
      }
      return false;
    }

    /**
     * Busca el vértice de un elemento en el árbol. Si no lo encuentra regresa
     * <tt>null</tt>.
     * @param elemento el elemento para buscar el vértice.
     * @return un vértice que contiene el elemento buscado si lo encuentra;
     *         <tt>null</tt> en otro caso.
     */
    public VerticeArbolBinario<T> busca(T elemento) {
        if(elemento==null || raiz==null)
          return null;
        Cola<Vertice> c = new Cola<>();
        c.mete(raiz);
        while(!c.esVacia()){
          Vertice v = c.saca();
          if(elemento.equals(v.elemento))
            return v;
          if(v.izquierdo!=null)
            c.mete(v.izquierdo);
          if(v.derecho!=null)
            c.mete(v.derecho);
        }
        return null;
    }

    /**
     * Regresa el vértice que contiene la raíz del árbol.
     * @return el vértice que contiene la raíz del árbol.
     * @throws NoSuchElementException si el árbol es vacío.
     */
    public VerticeArbolBinario<T> raiz() {
        if(raiz==null)
          throw new NoSuchElementException("There is no ground");
        return raiz;
    }

    /**
     * Nos dice si el árbol es vacío.
     * @return <code>true</code> si el árbol es vacío, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return raiz == null;
    }

    /**
     * Limpia el árbol de elementos, dejándolo vacío.
     */
    @Override public void limpia() {
        raiz=null;
        elementos=0;
    }

    /**
     * Compara el árbol con un objeto.
     * @param objeto el objeto con el que queremos comparar el árbol.
     * @return <code>true</code> si el objeto recibido es un árbol binario y los
     *         árboles son iguales; <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        ArbolBinario ab = (ArbolBinario) objeto;
        if(raiz!=null)
          return raiz.equals(ab.raiz);
        if(ab.raiz==null)
          return true;
        return false;
    }


    /**
     * Regresa una representación en cadena del árbol.
     * @return una representación en cadena del árbol.
     */
    @Override public String toString() {
      if(raiz==null)
        return "";
      return toString(raiz, "");
    }
    private String toString(Vertice v, String s){
      String arbol = v.toString()+"\n";
      if(v.izquierdo!=null&&v.derecho != null)
        arbol+=s+"├─›"+toString(v.izquierdo,s+"│  "  );
      else if(v.izquierdo!=null)
        arbol+=s+"└─›"+toString(v.izquierdo,s+"   "  );
      if(v.derecho!=null)
        arbol+=s+"└─»"+toString(v.derecho, s+"   ");

      return arbol;
    }

    /**
     * Convierte el vértice (visto como instancia de {@link
     * VerticeArbolBinario}) en vértice (visto como instancia de {@link
     * Vertice}). Método auxiliar para hacer esta audición en un único lugar.
     * @param vertice el vértice de árbol binario que queremos como vértice.
     * @return el vértice recibido visto como vértice.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         Vertice}.
     */
    protected Vertice vertice(VerticeArbolBinario<T> vertice) {
        return (Vertice)vertice;
    }

    private boolean esHoja(Vertice v){
      return v.izquierdo==null && v.derecho==null;
    }
}
