## Animazioni
    Utilizzare il metodo setCustomAnimation() (prima del metodo .replace())
    con quattro parametri al costruttore, quello a due parametri non funziona.

        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    Link: http://stackoverflow.com/questions/4817900/android-fragments-and-animation

## Material Design Header: Toolbar
    Per creare una Action Bar come nel Material Design si deve utilizzare il componente Toolbar e non più la ActionBar.
    Seguire questo tutorial: http://www.android4devs.com/2014/12/how-to-make-material-design-app.html

## Material Desgin Buttons:
    Bottoni che non occupano tutto lo spazio:
    http://stackoverflow.com/questions/34989961/how-to-remove-extra-padding-or-margin-in-material-design-button