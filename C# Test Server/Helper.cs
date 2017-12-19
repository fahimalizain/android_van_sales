using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;

namespace test_server
{
    public static class Helper
    {
        public static byte[] ReceiveLargeFile(this Socket socket, int length)
        {
            // send first the length of total bytes of the data to server
            // create byte array with the length that you've send to the server.
            byte[] data = new byte[length];


            int size = length; // lenght to reveive
            var total = 0; // total bytes to received
            var dataleft = size; // bytes that havend been received 

            // 1. check if the total bytes that are received < than the size you've send before to the server.
            // 2. if true read the bytes that have not been receive jet
            while (total < size)
            {
                // receive bytes in byte array data[]
                // from position of total received and if the case data that havend been received.
                var recv = socket.Receive(data, total, dataleft, SocketFlags.None);
                if (recv == 0) // if received data = 0 than stop reseaving
                {
                    data = null;
                    break;
                }
                total += recv;  // total bytes read + bytes that are received
                dataleft -= recv; // bytes that havend been received
            }
            return data; // return byte array and do what you have to do whith the bytes.
        }
    }
}
