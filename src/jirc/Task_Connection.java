/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jirc;

/**
 *
 * @author thepasto
 */

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.MotdEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.tasks.TaskImpl;
import jerklib.listeners.TaskCompletionListener;
import jerklib.util.*;
import java.util.*;


public class Task_Connection {
public ConnectionManager conman ;
public static Session session;
    public Task_Connection(String username,String password)
	{

		ConnectionManager conman = new ConnectionManager(new Profile(username,username,username+"_",username+"__"));
		session = conman.requestConnection("irc.freenode.net");


                final NickServAuthPlugin auth = new NickServAuthPlugin
                (
                        password, //password
                        'i', //mode char that indicates success
                        session, //session
                        Arrays.asList("#fives") // list of channels to join on success
                );

                auth.addTaskListener(new TaskCompletionListener()
                {
                        public void taskComplete(Object result)
                        {
                                if(result.equals(new Boolean(false)))
                                {
                                        //session.quit();
                                    System.out.println(Task_Connection.session.getUserModes());
                                }
                                else
                                {
                                        System.out.println("Authed!");
                                }
                        }
                });

                session.onEvent(auth, Type.CONNECT_COMPLETE , Type.MODE_EVENT);

		/* Add a Task to join a channel when the connection is complete
		   This task will only ever be notified of ConnectionCompleteEvents */
		session.onEvent(new TaskImpl("join_channels")
		{
			public void receiveEvent(IRCEvent e)
			{
				//e.getSession().join("#fives");
			}
		}, Type.CONNECT_COMPLETE);


		/* Add a Task to say hello */
		session.onEvent(new TaskImpl("hello")
		{
			public void receiveEvent(IRCEvent e)
			{
				JoinCompleteEvent jce = (JoinCompleteEvent)e;
				//jce.getChannel().say("Hello :D");
			}
		}, Type.JOIN_COMPLETE);



		/* Add a Task to be notified on MOTD and JoinComplete events */
		session.onEvent(new TaskImpl("motd_join")
		{
			public void receiveEvent(IRCEvent e)
			{
				if(e.getType() == Type.MOTD)
				{
					MotdEvent me = (MotdEvent)e;
					System.out.println(me.getMotdLine());
				}
				else
				{
					JoinCompleteEvent je = (JoinCompleteEvent)e;
					//je.getChannel().say("Yay tasks!");
                                        System.out.println(session.getUserModes());
				}
			}
		}, Type.MOTD , Type.JOIN_COMPLETE);



		/* Add a Task that will be notified of all events */
		session.onEvent(new TaskImpl("print")
		{
			public void receiveEvent(IRCEvent e)
			{
				System.out.println(e.getRawEventData());
                                System.out.println(session.getUserModes());
			}
		});

	}

	public static void main(String[] args)
	{
		new Task_Connection("thepasto","aerosmith");
	}

}
