package nl.saxion.robosim.test;


/**
 * Created by Damon & Joost van Dijk on 21-5-2015.
 */
public class MultiCastServerThreadTest {

//    /**
//     * Test to check whether a message has been sent, and received
//     */
//    @Test
//    public void testMultiCastSendInOrder() {
//        try {
//            new LogReader("2013-06-29-133738_odens_mrl.log");
//            //interval for sending messages
//            int waitingTime = 16;
//            //start sender thread
//            MultiCastServerThread m = new MultiCastServerThread(waitingTime);
//            m.start();
//
//            ArrayList<Long> timestamps = new ArrayList<>();
//            for(int i = 0; i < 40; i++) {
//                Thread.sleep(waitingTime + 5);
//                timestamps.add(m.getLastMessage().getTimeStamp());
//            }
//            Long previous = timestamps.get(0);
//            for(int i = 1; i < timestamps.size();i++) {
//                assertTrue(previous < timestamps.get(i));
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }



}