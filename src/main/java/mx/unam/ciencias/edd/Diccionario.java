package mx.unam.ciencias.edd;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para diccionarios (<em>hash tables</em>). Un diccionario generaliza el
 * concepto de arreglo, mapeando un conjunto de <em>llaves</em> a una colección
 * de <em>valores</em>.
 */
public class Diccionario<K, V> implements Iterable<V> {

    /* Clase interna privada para entradas. */
    private class Entrada {

        /* La llave. */
        public K llave;
        /* El valor. */
	      public V valor;
        /* Construye una nueva entrada. */
        public Entrada(K llave, V valor) {
            this.llave = llave;
	          this.valor = valor;
        }
    }

    /* Clase interna privada para iteradores. */
    private class Iterador {

        /* En qué lista estamos. */
        private int indice;
        /* Iterador auxiliar. */
        private Iterator<Entrada> iterador;

        /* Construye un nuevo iterador, auxiliándose de las listas del
         * diccionario. */
        public Iterador() {
            indice = 0;
            while(indice<entradas.length && entradas[indice]==null)
              indice++;
            Lista<Entrada> l= new Lista<Entrada>();
            iterador = l.iterator();
        }

        /* Nos dice si hay una siguiente entrada. */
        public boolean hasNext() {
          return indice<entradas.length || iterador.hasNext();
        }

        /* Regresa la siguiente entrada. */
        public Entrada siguiente() {
          if(!iterador.hasNext() && indice!=entradas.length)
            actualiza();
          if(iterador.hasNext())
            return iterador.next();
          return null;
        }

        private void actualiza(){
          if(indice<entradas.length && entradas[indice]!=null)
            iterador = entradas[indice++].iterator();
          while(indice<entradas.length && entradas[indice]==null)
            indice++;
        }
    }

    /* Clase interna privada para iteradores de llaves. */
    private class IteradorLlaves extends Iterador
        implements Iterator<K> {

        /* Regresa el siguiente elemento. */
        @Override public K next() {
          Entrada e = (Entrada) siguiente();
	        return (e!=null)? e.llave: null;
        }
    }

    /* Clase interna privada para iteradores de valores. */
    private class IteradorValores extends Iterador
        implements Iterator<V> {

        /* Regresa el siguiente elemento. */
        @Override public V next() {
          Entrada e = (Entrada) siguiente();
          return (e!=null)? e.valor: null;
        }
    }

    /** Máxima carga permitida por el diccionario. */
    public static final double MAXIMA_CARGA = 0.72;

    /* Capacidad mínima; decidida arbitrariamente a 2^6. */
    private static final int MINIMA_CAPACIDAD = 64;

    /* Dispersor. */
    private Dispersor<K> dispersor;
    /* Nuestro diccionario. */
    private Lista<Entrada>[] entradas;
    /* Número de valores. */
    private int elementos;

    /* Truco para crear un arreglo genérico. Es necesario hacerlo así por cómo
       Java implementa sus genéricos; de otra forma obtenemos advertencias del
       compilador. */
    @SuppressWarnings("unchecked")
    private Lista<Entrada>[] nuevoArreglo(int n) {
        return (Lista<Entrada>[])Array.newInstance(Lista.class, n);
    }

    /**
     * Construye un diccionario con una capacidad inicial y dispersor
     * predeterminados.
     */
    public Diccionario() {
        this(MINIMA_CAPACIDAD, (K llave) -> llave.hashCode());
    }

    /**
     * Construye un diccionario con una capacidad inicial definida por el
     * usuario, y un dispersor predeterminado.
     * @param capacidad la capacidad a utilizar.
     */
    public Diccionario(int capacidad) {
        this(capacidad, (K llave) -> llave.hashCode());
    }

    /**
     * Construye un diccionario con una capacidad inicial predeterminada, y un
     * dispersor definido por el usuario.
     * @param dispersor el dispersor a utilizar.
     */
    public Diccionario(Dispersor<K> dispersor) {
        this(MINIMA_CAPACIDAD, dispersor);
    }

    /**
     * Construye un diccionario con una capacidad inicial y un método de
     * dispersor definidos por el usuario.
     * @param capacidad la capacidad inicial del diccionario.
     * @param dispersor el dispersor a utilizar.
     */
    public Diccionario(int capacidad, Dispersor<K> dispersor) {
        capacidad = (capacidad>MINIMA_CAPACIDAD)? capacidad: MINIMA_CAPACIDAD;
        double log = Math.log(capacidad)/Math.log(2);
        if(Math.floor(log)==log)
          log = Math.floor(log)-1;
        int logInt = (int) Math.floor(log)+2;
        capacidad = (int) Math.pow(2, logInt);
        entradas = nuevoArreglo(capacidad);
        this.dispersor = dispersor;
    }

    /**
     * Agrega un nuevo valor al diccionario, usando la llave proporcionada. Si
     * la llave ya había sido utilizada antes para agregar un valor, el
     * diccionario reemplaza ese valor con el recibido aquí.
     * @param llave la llave para agregar el valor.
     * @param valor el valor a agregar.
     * @throws IllegalArgumentException si la llave o el valor son nulos.
     */
    public void agrega(K llave, V valor) {
      if(llave == null || valor == null)
        throw new IllegalArgumentException();

      Entrada e = getEntrada(llave);
      if(e!=null){
        e.valor = valor;
        return;
      }
      //la llave no estaba en el dicc
      elementos++;
      if(carga()>0.72)
          recrea();

      int i = getIndice(llave);
      if(entradas[i] == null)
    	    entradas[i] = new Lista<Entrada>();

      entradas[i].agrega(new Entrada(llave, valor));
    }

    /**
     * Regresa el valor del diccionario asociado a la llave proporcionada.
     * @param llave la llave para buscar el valor.
     * @return el valor correspondiente a la llave.
     * @throws IllegalArgumentException si la llave es nula.
     * @throws NoSuchElementException si la llave no está en el diccionario.
     */
    public V get(K llave) {
    	if(llave == null)
    	    throw new IllegalArgumentException();
      Entrada e = getEntrada(llave);
    	if(e == null)
    	    throw new NoSuchElementException();
    	return e.valor;
    }

    /**
     * Nos dice si una llave se encuentra en el diccionario.
     * @param llave la llave que queremos ver si está en el diccionario.
     * @return <tt>true</tt> si la llave está en el diccionario,
     *         <tt>false</tt> en otro caso.
     */
    public boolean contiene(K llave) {
      if(llave == null)
          return false;
      return getEntrada(llave) != null;
    }

    /**
     * Elimina el valor del diccionario asociado a la llave proporcionada.
     * @param llave la llave para buscar el valor a eliminar.
     * @throws IllegalArgumentException si la llave es nula.
     * @throws NoSuchElementException si la llave no se encuentra en
     *       el diccionario.
     */
    public void elimina(K llave) {
    	if(llave==null)
    	    throw new IllegalArgumentException();
      int i = getIndice(llave);
      Entrada e = getEntrada(llave, i);
    	if(e==null)
    	    throw new NoSuchElementException();
    	entradas[i].elimina(e);
      if(entradas[i].esVacia())
        entradas[i] = null;
    	elementos--;
    }

    /**
     * Nos dice cuántas colisiones hay en el diccionario.
     * @return cuántas colisiones hay en el diccionario.
     */
    public int colisiones() {
      int n  = 0;
      for (Lista<Entrada> l: entradas)
        if(l!=null && l.getElementos()>1)
          n++;
    	return n;
    }

    /**
     * Nos dice el máximo número de colisiones para una misma llave que tenemos
     * en el diccionario.
     * @return el máximo número de colisiones para una misma llave.
     */
    public int colisionMaxima() {
      int n  = 0;
      for (Lista<Entrada> l: entradas)
        if(l!=null && l.getElementos()>n)
          n = l.getElementos();
    	return n-1;
    }

    /**
     * Nos dice la carga del diccionario.
     * @return la carga del diccionario.
     */
    public double carga() {
	     return (double) elementos/entradas.length;
    }

    /**
     * Regresa el número de entradas en el diccionario.
     * @return el número de entradas en el diccionario.
     */
    public int getElementos() {
        return elementos;
    }

    /**
     * Nos dice si el diccionario es vacío.
     * @return <code>true</code> si el diccionario es vacío, <code>false</code>
     *         en otro caso.
     */
    public boolean esVacia() {
        return elementos == 0;
    }

    /**
     * Limpia el diccionario de elementos, dejándolo vacío.
     */
    public void limpia() {
        entradas = nuevoArreglo(MINIMA_CAPACIDAD);
        elementos = 0;
    }

    /**
     * Regresa una representación en cadena del diccionario.
     * @return una representación en cadena del diccionario.
     */
    @Override public String toString() {
        // Aquí va su código.
	      return "";
    }

    /**
     * Nos dice si el diccionario es igual al objeto recibido.
     * @param o el objeto que queremos saber si es igual al diccionario.
     * @return <code>true</code> si el objeto recibido es instancia de
     *         Diccionario, y tiene las mismas llaves asociadas a los mismos
     *         valores.
     */
    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        @SuppressWarnings("unchecked") Diccionario<K, V> d =
            (Diccionario<K, V>)o;
        return false;
    }

    /**
     * Regresa un iterador para iterar las llaves del diccionario. El
     * diccionario se itera sin ningún orden específico.
     * @return un iterador para iterar las llaves del diccionario.
     */
    public Iterator<K> iteradorLlaves() {
        return new IteradorLlaves();
    }

    /**
     * Regresa un iterador para iterar los valores del diccionario. El
     * diccionario se itera sin ningún orden específico.
     * @return un iterador para iterar los valores del diccionario.
     */
    @Override public Iterator<V> iterator() {
        return new IteradorValores();
    }

    private int getIndice(K key){
    	int mask = entradas.length-1;
    	int n = dispersor.dispersa(key);
    	return  n & mask;
    }

    private Entrada getEntrada(K key, int i){
    	if(entradas[i] == null)
    	    return null;
    	for(Entrada e: entradas[i])
    	    if(e.llave.equals(key))
                    return e;
    	return null;
    }

    private Entrada getEntrada(K key){
	     return getEntrada(key, getIndice(key));
    }

    private void recrea(){
  	Lista<Entrada>[] viejo = entradas;
    elementos=1;
  	entradas = nuevoArreglo(viejo.length*2);
  	for(Lista<Entrada> l: viejo)
  	    if(l != null)
      		for(Entrada e: l)
      		    agrega(e.llave, e.valor);
    }
}
