package mx.unam.ciencias.edd;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase genérica para listas doblemente ligadas.</p>
 *
 * <p>Las listas nos permiten agregar elementos al inicio o final de la lista,
 * eliminar elementos de la lista, comprobar si un elemento está o no en la
 * lista, y otras operaciones básicas.</p>
 *
 * <p>Las listas no aceptan a <code>null</code> como elemento.</p>
 *
 * @param <T> El tipo de los elementos de la lista.
 */
public class Lista<T> implements Coleccion<T> {

    /* Clase interna privada para nodos. */
    private class Nodo {
        /* El elemento del nodo. */
        public T elemento;
        /* El nodo anterior. */
        public Nodo anterior;
        /* El nodo siguiente. */
        public Nodo siguiente;

        /* Construye un nodo con un elemento. */
        public Nodo(T elemento) {
          this.elemento = elemento;
        }
    }

    /* Clase interna privada para iteradores. */
    private class Iterador implements IteradorLista<T> {
        /* El nodo anterior. */
        public Nodo anterior;
        /* El nodo siguiente. */
        public Nodo siguiente;

        /* Construye un nuevo iterador. */
        public Iterador() {
            siguiente = cabeza;

        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
          return  siguiente != null;
        }

        /* Nos da el elemento siguiente. */
        @Override public T next() {
          if(siguiente == null)
            throw new NoSuchElementException();
          anterior = siguiente;
          siguiente = siguiente.siguiente;
          return anterior.elemento;
        }

        /* Nos dice si hay un elemento anterior. */
        @Override public boolean hasPrevious() {
          return anterior != null;
        }

        /* Nos da el elemento anterior. */
        @Override public T previous() {
          if(anterior == null)
            throw new NoSuchElementException();
          siguiente = anterior;
          anterior = anterior.anterior;
          return siguiente.elemento;
        }

        /* Mueve el iterador al inicio de la lista. */
        @Override public void start() {
          anterior = null;
          siguiente = cabeza;
        }

        /* Mueve el iterador al final de la lista. */
        @Override public void end() {
          anterior = rabo;
          siguiente = null;
        }
    }

    /* Primer elemento de la lista. */
    private Nodo cabeza;
    /* Último elemento de la lista. */
    private Nodo rabo;
    /* Número de elementos en la lista. */
    private int longitud;

    /**
     * Regresa la longitud de la lista. El método es idéntico a {@link
     * #getElementos}.
     * @return la longitud de la lista, el número de elementos que contiene.
     */
    public int getLongitud() {
      return longitud;
    }

    /**
     * Regresa el número elementos en la lista. El método es idéntico a {@link
     * #getLongitud}.
     * @return el número elementos en la lista.
     */
    @Override public int getElementos() {
      return longitud;
    }

    /**
     * Nos dice si la lista es vacía.
     * @return <code>true</code> si la lista es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
      return rabo==null;
    }

    /**
     * Agrega un elemento a la lista. Si la lista no tiene elementos, el
     * elemento a agregar será el primero y último. El método es idéntico a
     * {@link #agregaFinal}.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento) {
      agregaFinal(elemento);
    }

    /**
     * Agrega un elemento al final de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaFinal(T elemento) {
      if(elemento == null)
        throw new IllegalArgumentException();
      Nodo nuevoNodo = new Nodo(elemento);
      if(rabo==null)
        cabeza= nuevoNodo;
      else{
        nuevoNodo.anterior = rabo;
        rabo.siguiente = nuevoNodo;
      }
      rabo = nuevoNodo;
      longitud++;
    }

    /**
     * Agrega un elemento al inicio de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaInicio(T elemento) {
      if(elemento == null)
        throw new IllegalArgumentException();
      Nodo nuevoNodo = new Nodo(elemento);
      if(rabo==null)
        rabo = nuevoNodo;
      else{
        nuevoNodo.siguiente = cabeza;
        cabeza.anterior = nuevoNodo;
      }
      cabeza = nuevoNodo;
      longitud++;
    }

    /**
     * Inserta un elemento en un índice explícito.
     *
     * Si el índice es menor o igual que cero, el elemento se agrega al inicio
     * de la lista. Si el índice es mayor o igual que el número de elementos en
     * la lista, el elemento se agrega al fina de la misma. En otro caso,
     * después de mandar llamar el método, el elemento tendrá el índice que se
     * especifica en la lista.
     * @param i el índice dónde insertar el elemento. Si es menor que 0 el
     *          elemento se agrega al inicio de la lista, y si es mayor o igual
     *          que el número de elementos en la lista se agrega al final.
     * @param elemento el elemento a insertar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void inserta(int i, T elemento) {
      if (elemento==null)
          throw new IllegalArgumentException();
      if(i<=0)
          agregaInicio(elemento);
      else if(i>=longitud)
          agregaFinal(elemento);
      else
          inserta(--i, new Nodo(elemento), cabeza.siguiente);
    }
    private void inserta(int i, Nodo nuevoNodo, Nodo nodo){
        if(i==0){
            nuevoNodo.siguiente = nodo;
            nuevoNodo.anterior = nodo.anterior;
            nodo.anterior.siguiente=nuevoNodo;
            nodo.anterior = nuevoNodo;
            longitud++;
            return;
        }
        inserta(--i, nuevoNodo, nodo.siguiente);
    }

    /**
     * Elimina un elemento de la lista. Si el elemento no está contenido en la
     * lista, el método no la modifica.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
      borraNodo(buscaNodo(elemento, cabeza));
    }

    /**
     * Elimina el primer elemento de la lista y lo regresa.
     * @return el primer elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaPrimero() {
      if(esVacia())
         throw new NoSuchElementException();
       return borraNodo(cabeza);
    }

    /**
     * Elimina el último elemento de la lista y lo regresa.
     * @return el último elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaUltimo() {
      if(esVacia())
        throw new NoSuchElementException();
      return borraNodo(rabo);
    }

    /**
     * Nos dice si un elemento está en la lista.
     * @param elemento el elemento que queremos saber si está en la lista.
     * @return <tt>true</tt> si <tt>elemento</tt> está en la lista,
     *         <tt>false</tt> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
      return (elemento == null)? false: contiene(elemento, cabeza);
    }
    private boolean contiene(T e, Nodo n){
      if(n==null)
        return false;
      return e.equals(n.elemento)? true: contiene(e, n.siguiente);
    }

    /**
     * Regresa la reversa de la lista.
     * @return una nueva lista que es la reversa la que manda llamar el método.
     */
    public Lista<T> reversa() {
      return reversa(new Lista<T>(), rabo);
    }
    private Lista<T> reversa(Lista<T> l, Nodo n){
      if(n==null)
        return l;
      l.agregaFinal(n.elemento);
      return reversa(l, n.anterior);
    }

    /**
     * Regresa una copia de la lista. La copia tiene los mismos elementos que la
     * lista que manda llamar el método, en el mismo orden.
     * @return una copiad de la lista.
     */
    public Lista<T> copia() {
      Lista<T> l = new Lista<T>();
      Nodo n = cabeza;
      while(n != null){
        l.agregaFinal(n.elemento);
        n=n.siguiente;
      }
      return l;
    }

    /**
     * Limpia la lista de elementos, dejándola vacía.
     */
    @Override public void limpia() {
      cabeza = null;
      rabo = null;
      longitud = 0;
    }

    /**
     * Regresa el primer elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getPrimero() {
      if(rabo==null)
        throw new NoSuchElementException();
      return cabeza.elemento;
    }

    /**
     * Regresa el último elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getUltimo() {
      if(rabo == null)
        throw new NoSuchElementException();
      return rabo.elemento;
    }

    /**
     * Regresa el <em>i</em>-ésimo elemento de la lista.
     * @param i el índice del elemento que queremos.
     * @return el <em>i</em>-ésimo elemento de la lista.
     * @throws ExcepcionIndiceInvalido si <em>i</em> es menor que cero o mayor o
     *         igual que el número de elementos en la lista.
     */
    public T get(int i) {
      if(i<0 || i>=longitud)
        throw new ExcepcionIndiceInvalido("el valor es muy grande o muy pequeño");
      return get(i, cabeza);
    }
    private T get(int i, Nodo n){
      return (i--==0)? n.elemento: get(i, n.siguiente);
    }

    /**
     * Regresa el índice del elemento recibido en la lista.
     * @param elemento el elemento del que se busca el índice.
     * @return el índice del elemento recibido en la lista, o -1 si el elemento
     *         no está contenido en la lista.
     */
    public int indiceDe(T elemento) {
      if(elemento==null)
        return -1;
      return indiceDe(elemento, cabeza);
    }
    private int indiceDe(T e, Nodo n){
      if(n==null)
        return -longitud-1;
      return (e.equals(n.elemento))? 0: 1+indiceDe(e, n.siguiente);
    }

    /**
     * Regresa una representación en cadena de la lista.
     * @return una representación en cadena de la lista.
     */
    @Override public String toString() {
      if(rabo==null)
        return "[]";
      return  "["+cabeza.elemento+toString(cabeza.siguiente);
    }
    private String toString(Nodo n){
      return (n!=null)? ", "+n.elemento+toString(n.siguiente): "]";
    }

    /**
     * Nos dice si la lista es igual al objeto recibido.
     * @param objeto el objeto con el que hay que comparar.
     * @return <tt>true</tt> si la lista es igual al objeto recibido;
     *         <tt>false</tt> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
      if (objeto == null || getClass() != objeto.getClass())
          return false;
      Lista lista = (Lista)objeto;
      return longitud != lista.longitud? false: equals(cabeza, lista.cabeza);
    }
    private boolean equals(Lista.Nodo n, Lista.Nodo otroN){
      if(n==null)
        return true;
      return (n.elemento.equals(otroN.elemento))?
        equals(n.siguiente, otroN.siguiente):
        false;
    }

    /**
     * Regresa un iterador para recorrer la lista en una dirección.
     * @return un iterador para recorrer la lista en una dirección.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Regresa un iterador para recorrer la lista en ambas direcciones.
     * @return un iterador para recorrer la lista en ambas direcciones.
     */
    public IteradorLista<T> iteradorLista() {
        return new Iterador();
    }

    /**
     * Regresa una copia de la lista, pero ordenada. Para poder hacer el
     * ordenamiento, el método necesita una instancia de {@link Comparator} para
     * poder comparar los elementos de la lista.
     * @param comparador el comparador que la lista usará para hacer el
     *                   ordenamiento.
     * @return una copia de la lista, pero ordenada.
     */
    public Lista<T> mergeSort(Comparator<T> comparador) {
      if(longitud<2)
          return this;
      Lista<T> izq = new Lista<>(), der = new Lista<>();
      int mitad = (int) longitud/2;
      for (T t: this)
        if(--mitad>-1)
          der.agregaFinal(t);
        else
          izq.agregaFinal(t);
      return mergeSort(izq.mergeSort(comparador), der.mergeSort(comparador), comparador);

    }
    private Lista<T> mergeSort(Lista<T> a, Lista<T> b, Comparator<T> c) {
       Lista<T> l = new Lista<T>();
       Nodo alfa = a.cabeza, beta = b.cabeza;
       while(alfa!=null && beta!=null)
         if(c.compare(alfa.elemento, beta.elemento) < 0)
           alfa= l.avanza(alfa);
         else
           beta= l.avanza(beta);
       while(alfa!=null)
         alfa= l.avanza(alfa);
       while(beta!=null)
         beta= l.avanza(beta);

       return l;
    }

    /**
     * Regresa una copia de la lista recibida, pero ordenada. La lista recibida
     * tiene que contener nada más elementos que implementan la interfaz {@link
     * Comparable}.
     * @param <T> tipo del que puede ser la lista.
     * @param lista la lista que se ordenará.
     * @return una copia de la lista recibida, pero ordenada.
     */
    public static <T extends Comparable<T>>
    Lista<T> mergeSort(Lista<T> lista) {
        return lista.mergeSort((a, b) -> a.compareTo(b));
    }

    /**
     * Busca un elemento en la lista ordenada, usando el comparador recibido. El
     * método supone que la lista está ordenada usando el mismo comparador.
     * @param elemento el elemento a buscar.
     * @param comparador el comparador con el que la lista está ordenada.
     * @return <tt>true</tt> si el elemento está contenido en la lista,
     *         <tt>false</tt> en otro caso.
     */
    public boolean busquedaLineal(T elemento, Comparator<T> comparador) {
        return busquedaLineal(elemento, comparador, cabeza);
    }

    /**
     * Busca un elemento en una lista ordenada. La lista recibida tiene que
     * contener nada más elementos que implementan la interfaz {@link
     * Comparable}, y se da por hecho que está ordenada.
     * @param <T> tipo del que puede ser la lista.
     * @param lista la lista donde se buscará.
     * @param elemento el elemento a buscar.
     * @return <tt>true</tt> si el elemento está contenido en la lista,
     *         <tt>false</tt> en otro caso.
     */
    public static <T extends Comparable<T>>
    boolean busquedaLineal(Lista<T> lista, T elemento) {
        return lista.busquedaLineal(elemento, (a, b) -> a.compareTo(b));
    }


    private boolean busquedaLineal(T e, Comparator<T> c, Nodo n){
      if(n==null)
        return false;
      return (c.compare(n.elemento, e) == 0) ? true: busquedaLineal(e, c, n.siguiente);
    }


    private T borraNodo(Nodo n){
        if (n==null)
          return null;
        T exNodo = n.elemento;
        if(rabo==n && cabeza==n){
            rabo=null;
            cabeza=null;
        }
        else if(n==cabeza){
            cabeza=n.siguiente;
            n.siguiente.anterior=null;
        }
        else if(n==rabo){
            rabo=n.anterior;
            n.anterior.siguiente=null;
        }
        else{
            n.anterior.siguiente=n.siguiente;
            n.siguiente.anterior=n.anterior;
        }
        longitud--;
        return exNodo;
    }
    private Nodo buscaNodo(T e, Nodo n){
      if(n==null)
        return null;
      return (e.equals(n.elemento))? n: buscaNodo(e, n.siguiente);
    }
    private Nodo avanza(Nodo n){
      agregaFinal(n.elemento);
      return n.siguiente;
    }
}
