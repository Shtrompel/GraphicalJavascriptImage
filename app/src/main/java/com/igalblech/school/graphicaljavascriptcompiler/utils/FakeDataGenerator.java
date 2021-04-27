package com.igalblech.school.graphicaljavascriptcompiler.utils;

import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.RenderColorFormat;
import com.igalblech.school.graphicaljavascriptcompiler.utils.userdata.UserData;

import org.joda.time.Instant;
import java.util.Date;
import java.util.Random;

/**
 * Used for debugging
 */
public class FakeDataGenerator {

    public static final Random rand = new Random ( 0 );
    public static Instant dateBetween( long start, long end) {
        long l = rand.nextLong ();
        l = l % (start - end) + end;
        return Instant.ofEpochMilli ( l );
    }

    public static String randStr(int l) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'

        byte[] array = new byte[l]; // length is bounded by 7
        for (int i = 0; i < l; ++i) {
            array[i] = (byte)(rand.nextInt ( rightLimit - leftLimit ) + leftLimit);
        }
        return new String ( array );
    }

    public static String randS(int l) {
        return randStr(rand.nextInt ( l ));
    }

    public static UserData randomUserData() {
        return new UserData ( randS(16),randS(16),randS(16),randS(16) );
    }

    public static ProjectSettings debugRandomSettings() {

        Instant dateCreated = dateBetween(
                0, new Date ( 140, 12, 30 ).getTime());
        Instant lastUpdated = dateBetween(
                0, new Date ( 140, 12, 30 ).getTime());

        ProjectSettings s = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            s = new ProjectSettings (
                    randomUserData(),
                    new RenderColorFormat (rand.nextInt ( 3 ),
                            rand.nextInt ( 16 ), rand.nextBoolean (), rand.nextBoolean ()),
                    rand.nextInt (512),
                    rand.nextInt (512)
            );
        }
        assert s != null;
        s.setCode ( randS(512) );
        s.setProjectInfo ( randS(32), randS(512) );
        s.setDates (dateCreated, lastUpdated);

        for (int i = 0; i < rand.nextInt (32); i++)
            s.addVote ( rand.nextFloat () * 10.0f );

        for (int i = 0; i < rand.nextInt(256); i++)
            s.addView ();

        /*s.width = rand.nextInt (512); s.height = rand.nextInt (512);
        s.code = randS(512);
        s.dateCreated = new Date ( ThreadLocalRandom.current()
                .nextLong(new Date ( 0, 1, 1 ).getTime(), new Date ( 140, 12, 30 ).getTime()));
        s.lastUpdated = new Date( ThreadLocalRandom.current()
                .nextLong(new Date ( 0, 1, 1 ).getTime(), new Date ( 140, 12, 30 ).getTime()));
        s.title = randS(32);
        s.description = randS(512);
        s.userData = randomUserData();
        s.format = new RenderColorFormat (rand.nextInt ( 3 ),
                rand.nextInt ( 16 ), rand.nextBoolean (), rand.nextBoolean ());*/
        return s;
    }

}
