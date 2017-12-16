using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace test_server
{
    class Program
    {
        static void Main(string[] args)
        {
            CancellationTokenSource _cancel = new CancellationTokenSource();
            Thread t = new Thread(() =>
            {
                TcpListener listener = new TcpListener(new IPEndPoint(IPAddress.Loopback, 18565));
                listener.Start();
                Console.WriteLine("Started Listening..");
                while (true)
                {
                    if (_cancel.IsCancellationRequested)
                        break;

                    if (listener.Pending())
                    {
                        Socket s = listener.AcceptSocket();
                        new Thread(() =>
                        {                            
                            handleConnection(s);
                        }).Start();
                    }
                }
                Console.WriteLine("Cancellation Requested");
                listener.Stop();
            });
            t.Start();
            Console.ReadKey(true);
            _cancel.Cancel();   
        }

        static void handleConnection(Socket s)
        {
            Console.WriteLine("Incoming connection from " + s.RemoteEndPoint.ToString());

            s.Close();
        }
    }    
}
