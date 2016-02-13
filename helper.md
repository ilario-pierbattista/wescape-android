## Animazioni
    Utilizzare il metodo setCustomAnimation() (prima del metodo .replace())
    con quattro parametri al costruttore, quello a due parametri non funziona.

        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    Link: http://stackoverflow.com/questions/4817900/android-fragments-and-animation