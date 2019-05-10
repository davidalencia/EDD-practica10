package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para montículos mínimos (<i>min heaps</i>).
 */
public class MonticuloMinimo<T extends ComparableIndexable<T>>
    implements Coleccion<T>, MonticuloDijkstra<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Índice del iterador. */
        private int indice;

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return indice < elementos;
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            if(indice >= elementos)
              throw new NoSuchElementException();
            return arbol[indice++];
        }
    }

    /* Clase estática privada para adaptadores. */
    private static class Adaptador<T  extends Comparable<T>>
        implements ComparableIndexable<Adaptador<T>> {

        /* El elemento. */
        private T elemento;
        /* El índice. */
        private int indice;

        /* Crea un nuevo comparable indexable. */
        public Adaptador(T elemento) {
            this.elemento = elemento;
            indice =-1;
        }

        /* Regresa el índice. */
        @Override public int getIndice() {
            return indice;
        }

        /* Define el índice. */
        @Override public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Compara un adaptador con otro. */
        @Override public int compareTo(Adaptador<T> adaptador) {
            return elemento.compareTo(adaptador.elemento);
        }
    }

    /* El número de elementos en el arreglo. */
    private int elementos;
    /* Usamos un truco para poder utilizar arreglos genéricos. */
    private T[] arbol;

    /* Truco para crear arreglos genéricos. Es necesario hacerlo así por cómo
       Java implementa sus genéricos; de otra forma obtenemos advertencias del
       compilador. */
    @SuppressWarnings("unchecked") private T[] nuevoArreglo(int n) {
        return (T[])(new ComparableIndexable[n]);
    }

    /**
     * Constructor sin parámetros. Es más eficiente usar {@link
     * #MonticuloMinimo(Coleccion)} o {@link #MonticuloMinimo(Iterable,int)},
     * pero se ofrece este constructor por completez.
     */
    public MonticuloMinimo() {
        arbol = nuevoArreglo(100); /* 100 es arbitrario. */
    }

    /**
     * Constructor para montículo mínimo que recibe una colección. Es más barato
     * construir un montículo con todos sus elementos de antemano (tiempo
     * <i>O</i>(<i>n</i>)), que el insertándolos uno por uno (tiempo
     * <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param coleccion la colección a partir de la cuál queremos construir el
     *                  montículo.
     */
    public MonticuloMinimo(Coleccion<T> coleccion) {
        this(coleccion, coleccion.getElementos());
    }

    /**
     * Constructor para montículo mínimo que recibe un iterable y el número de
     * elementos en el mismo. Es más barato construir un montículo con todos sus
     * elementos de antemano (tiempo <i>O</i>(<i>n</i>)), que el insertándolos
     * uno por uno (tiempo <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param iterable el iterable a partir de la cuál queremos construir el
     *                 montículo.
     * @param n el número de elementos en el iterable.
     */
    public MonticuloMinimo(Iterable<T> iterable, int n) {
        arbol = nuevoArreglo(n);
        elementos = 0;
        for (T e: iterable){
          arbol[elementos] = e;
          e.setIndice(elementos);
          elementos++;
        }
        for(int teta = n/2; teta>=0; teta--)
          acomodaAbajo(teta);
    }

    /**
     * Agrega un nuevo elemento en el montículo.
     * @param elemento el elemento a agregar en el montículo.
     */
    @Override public void agrega(T elemento) {
        if(elementos==arbol.length){
          T[] arbolViejo = arbol;
          arbol = nuevoArreglo(elementos+100);
          elementos = 0;
          for (T e: arbolViejo){
            arbol[elementos] = e;
            e.setIndice(elementos);
            elementos++;
          }
        }
        arbol[elementos] = elemento;
        elemento.setIndice(elementos);
        reordena(elemento);
        elementos++;
    }

    /**
     * Elimina el elemento mínimo del montículo.
     * @return el elemento mínimo del montículo.
     * @throws IllegalStateException si el montículo es vacío.
     */
    @Override public T elimina() {
      if(elementos<1)
        throw new IllegalStateException();
      elementos--;
      T r = arbol[0];
      swap(0, elementos);
      arbol[elementos]=null;
      reordena(0);
      r.setIndice(-1);
      return r;
    }

    /**
     * Elimina un elemento del montículo.
     * @param elemento a eliminar del montículo.
     */
    @Override public void elimina(T elemento) {
      int i = elemento.getIndice();
      if(i>=elementos || i <0)
        return;
      elementos--;
      swap(i, elementos);
      arbol[elementos]=null;
      if(arbol[i]!=null)
        reordena(i);

      elemento.setIndice(-1);
    }

    /**
     * Nos dice si un elemento está contenido en el montículo.
     * @param elemento el elemento que queremos saber si está contenido.
     * @return <code>true</code> si el elemento está contenido,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
      return contiene(0, elemento);
    }
    private boolean contiene(int i, T e){
      if(i==elementos)
        return false;
      return arbol[i].equals(e)? true: contiene(++i, e);
    }

    /**
     * Nos dice si el montículo es vacío.
     * @return <tt>true</tt> si ya no hay elementos en el montículo,
     *         <tt>false</tt> en otro caso.
     */
    @Override public boolean esVacia() {
        return arbol[0]==null;
    }

    /**
     * Limpia el montículo de elementos, dejándolo vacío.
     */
    @Override public void limpia() {
        for (int alfa = 0; alfa<elementos; alfa++)
          arbol[alfa]=null;
        elementos = 0;
    }

   /**
     * Reordena un elemento en el árbol.
     * @param elemento el elemento que hay que reordenar.
     */
    @Override public void reordena(T elemento) {
        reordena(elemento.getIndice());
    }
    private void reordena(int i){
      acomodaArriba(i);
      acomodaAbajo(i);
    }

    /**
     * Regresa el número de elementos en el montículo mínimo.
     * @return el número de elementos en el montículo mínimo.
     */
    @Override public int getElementos() {
        return elementos;
    }

    /**
     * Regresa el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @param i el índice del elemento que queremos, en <em>in-order</em>.
     * @return el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @throws NoSuchElementException si i es menor que cero, o mayor o igual
     *         que el número de elementos.
     */
    @Override public T get(int i) {
      if(i<0 || i>=elementos)
        throw new NoSuchElementException();
      return arbol[i];
    }

    /**
     * Regresa una representación en cadena del montículo mínimo.
     * @return una representación en cadena del montículo mínimo.
     */
    @Override public String toString() {
      String s = "";
      for (int alfa = 0; alfa<elementos; alfa++)
        s += String.format("%s, ", arbol[alfa].toString());
      return s;
    }

    /**
     * Nos dice si el montículo mínimo es igual al objeto recibido.
     * @param objeto el objeto con el que queremos comparar el montículo mínimo.
     * @return <code>true</code> si el objeto recibido es un montículo mínimo
     *         igual al que llama el método; <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") MonticuloMinimo<T> monticulo =
            (MonticuloMinimo<T>)objeto;
        if(elementos!=monticulo.elementos)
          return false;
        for (int alfa = 0; alfa<elementos; alfa++)
          if(!arbol[alfa].equals(monticulo.arbol[alfa]))
            return false;
        return true;

    }

    /**
     * Regresa un iterador para iterar el montículo mínimo. El montículo se
     * itera en orden BFS.
     * @return un iterador para iterar el montículo mínimo.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Ordena la colección usando HeapSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param coleccion la colección a ordenar.
     * @return una lista ordenada con los elementos de la colección.
     */
    public static <T extends Comparable<T>>
    Lista<T> heapSort(Coleccion<T> coleccion) {
        MonticuloMinimo<Adaptador<T>> mm = new MonticuloMinimo<>();
        for (T e: coleccion)
          mm.agrega(new Adaptador<>(e));

        Lista<T> l = new Lista<T>();
        for (int alfa = 0; alfa<coleccion.getElementos(); alfa++)
          l.agrega(mm.elimina().elemento);

        return l;
    }

    private void acomodaAbajo(int i){
      if(i>=elementos)
        return;
      int izq = 2*i+1;
      int der = 2*i+2;
      boolean vsIzq = false, vsDer = false, izqVsDer = false;
      if(izq<elementos)
        vsIzq = arbol[i].compareTo(arbol[izq])>=0;
      if(der<elementos)
        vsDer = arbol[i].compareTo(arbol[der])>=0;
      if(izq<elementos && der<elementos)
        izqVsDer = arbol[izq].compareTo(arbol[der])>=0;
      if(vsIzq && vsDer)
        if(izqVsDer){
          swap(i, der);
          acomodaAbajo(der);
        }
        else{
          swap(i, izq);
          acomodaAbajo(izq);
        }
      else if(vsIzq){
        swap(i, izq);
        acomodaAbajo(izq);
      }
      else if(vsDer){
        swap(i, der);
        acomodaAbajo(der);
      }
    }
    private void acomodaArriba(int i){
      int padre = (i-1)/2;
      if(i<=0 || padre<0 || padre >= elementos || arbol[i].compareTo(arbol[padre])>=0)
        return;
      swap(i, padre);
      acomodaArriba(padre);
    }
    private void swap(int i, int j){
      arbol[i].setIndice(j);
      arbol[j].setIndice(i);
      T e = arbol[i];
      arbol[i] = arbol[j];
      arbol[j] = e;
    }
}
