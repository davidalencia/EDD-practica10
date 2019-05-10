package mx.unam.ciencias.edd;

/**
 * Clase para pilas genéricas.
 */
public class Pila<T> extends MeteSaca<T> {

    /**
     * Regresa una representación en cadena de la pila.
     * @return una representación en cadena de la pila.
     */
    @Override public String toString() {
        return toString(cabeza);
    }
    private String toString(Nodo n){
      return (n!=null)? n.elemento+"\n"+toString(n.siguiente): "";
    }

    /**
     * Agrega un elemento al tope de la pila.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void mete(T elemento) {
      if(elemento==null)
        throw new IllegalArgumentException();
      Nodo c = cabeza;
      cabeza = new Nodo(elemento);
      cabeza.siguiente=c;
    }
}
