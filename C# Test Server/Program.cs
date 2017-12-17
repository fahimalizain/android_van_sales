using Newtonsoft.Json.Linq;
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

            // Reading identity
            JObject obj = readJson(s);

            // Sending test data
            JObject data = getTestData();
            sendJson(s, data);

            Console.WriteLine("Connection Handled Successfully");
            Console.WriteLine("-------------------------------------");
            s.Close();
        }

        static void sendJson(Socket s, JObject root)
        {
            byte[] data = getBytes(root.ToString());
            byte[] data_len = getBytes(data.Length);
            s.Send(data_len);
            s.Send(data);
            Console.WriteLine("Sending " + data.Length + " Bytes");            
        }

        static JObject readJson(Socket s)
        {
            // Initial 4 byte (int) as size of the rest of string
            byte[] response = new byte[4];
            int receivedSize = s.Receive(response, 4, SocketFlags.None);
            CheckSize(4, receivedSize);
                        
            int expectedSize = getInt(response);
            response = new byte[expectedSize];

            receivedSize = s.Receive(response, expectedSize, SocketFlags.None);
            CheckSize(expectedSize, receivedSize);
            // Big Endian
            string data = getString(response);

            Console.WriteLine("JSON Read :\n" + data);
            return JObject.Parse(data);           
        }

        public static int SwapEndianness(int value)
        {
            var b1 = (value >> 0) & 0xff;
            var b2 = (value >> 8) & 0xff;
            var b3 = (value >> 16) & 0xff;
            var b4 = (value >> 24) & 0xff;

            return b1 << 24 | b2 << 16 | b3 << 8 | b4 << 0;
        }

        static void CheckSize(int expected, int recieved)
        {
            if (expected != recieved)
                throw new Exception("Network Expectation Failure");
        }

        static int getInt(byte[] data)
        {
            int num = BitConverter.ToInt32(data, 0);
            return IPAddress.NetworkToHostOrder(num);
        }

        static byte[] getBytes(int num)
        {
            num = IPAddress.HostToNetworkOrder(num);
            return BitConverter.GetBytes(num);
        }

        // Returns string from an array of Unicode-16 Big Endian Byte Ordered Data
        static string getString(byte[] data)
        {
            return new string(Encoding.BigEndianUnicode.GetChars(data));
        }

        static byte[] getBytes(string data)
        {
            return Encoding.BigEndianUnicode.GetBytes(data);
        }

        static JObject getTestData()
        {
            Random r = new Random();

            JObject root = new JObject();

            JArray items = new JArray();
            JObject stock_details = new JObject();
            JArray unit_and_price = new JArray();
            
            for (int i = 1; i < 100; i++)
            {

                string item_code = "ITEM" + i.ToString().PadLeft(5, '0');
                JObject item_obj = new JObject();
                item_obj["item_code"] = item_code;
                item_obj["item_name"] = "Item 1" + i.ToString().PadLeft(4, '0');
                item_obj["barcode"] = r.Next(100000, 1000000);
                items.Add(item_obj);
                
                stock_details[item_code] = r.Next(30, 300);

                JArray unitArray = new JArray();

                int unit_price = r.Next(40, 300);
                unit_and_price.Add(
                    new JObject(
                        new JProperty("unit_code", "pcs"),
                        new JProperty("unit_name", "Pcs"),
                        new JProperty("item_code", item_code),
                        new JProperty("conversion_factor", 1),
                        new JProperty("price", unit_price)
                    )
                );
                int conversion_factor = r.Next(5, 20);
                unit_and_price.Add(
                    new JObject(
                        new JProperty("unit_code", "box"),
                        new JProperty("unit_name", "Box"),
                        new JProperty("item_code", item_code),
                        new JProperty("conversion_factor", conversion_factor),
                        new JProperty("price", unit_price * conversion_factor - Math.Min(unit_price * conversion_factor - 10, r.Next(20, 40)))
                    )
                );
            }            

            
            root["items"] = items;
            root["stock_details"] = stock_details;
            root["unit_and_price"] = unit_and_price;

            return root;
        }
    }    
}