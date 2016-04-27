/* echo-client-udp.c */

/* Simple UDP echo client - tries to send everything read from stdin
   as a single datagram (MAX 1MB)*/

#include <stdio.h>      /* standard C i/o facilities */
#include <stdlib.h>     /* needed for atoi() */
#include <unistd.h>     /* defines STDIN_FILENO, system calls,etc */
#include <sys/types.h>  /* system data type definitions */
#include <sys/socket.h> /* socket specific definitions */
#include <netinet/in.h> /* INET constants and stuff */
#include <arpa/inet.h>  /* IP address conversion stuff */
#include <netdb.h>      /* gethostbyname */
#include <string.h>

/* get_stdin reads from standard input until EOF is found,
   or the maximum bytes have been read.
*/

int get_stdin( char *buf, int maxlen ) {
  int i=0;
  int n;

  while ( (n=read(STDIN_FILENO,buf+i,maxlen-i)) > 0 ) {
    i+=n;
    if (i==maxlen) break;
  }

  if (n!=0) {
    perror("Error reading stdin");
    exit(1);
  }

  /* return the number of bytes read including the last read */
  return(i);
}


/* client program:

   The following must passed in on the command line:
      hostname of the server (argv[1])
      port number of the server (argv[2])
*/

#define MAXBUF 10*1024

int main( int argc, char **argv ) {
  int sk;
  struct sockaddr_in server;
  struct hostent *hp;
  char buf[MAXBUF];
  int buf_len;
  int n_sent;
  int n_read;


  /* Make sure we have the right number of command line args */

  if (argc!=3) {
    printf("Usage: %s <server name> <port number>\n",argv[0]);
    exit(0);
  }

  /* create a socket
     IP protocol family (PF_INET)
     UDP (SOCK_DGRAM)
  */

  if ((sk = socket( PF_INET, SOCK_DGRAM, 0 )) < 0)
    {
      printf("Problem creating socket\n");
      exit(1);
    }

  /* Using UDP we don't need to call bind unless we care what our
     port number is - most clients don't care */

  /* now create a sockaddr that will be used to contact the server

     fill in an address structure that will be used to specify
     the address of the server we want to connect to

     address family is IP  (AF_INET)

     server IP address is found by calling gethostbyname with the
     name of the server (entered on the command line)

     server port number is argv[2] (entered on the command line)
  */

  server.sin_family = AF_INET;
  if ((hp = gethostbyname(argv[1]))==0) {
    printf("Invalid or unknown host\n");
    exit(1);
  }

  /* copy the IP address into the sockaddr
     It is already in network byte order
  */

  memcpy( &server.sin_addr.s_addr, hp->h_addr, hp->h_length);

  /* establish the server port number - we must use network byte order! */
  server.sin_port = htons(atoi(argv[2]));

  /* read everything possible */
  buf_len = get_stdin(buf,MAXBUF);
  printf("Got %d bytes from stdin - sending...\n",buf_len);

  /* send it to the echo server */

  n_sent = sendto(sk,buf,buf_len,0,
                  (struct sockaddr*) &server,sizeof(server));

  if (n_sent<0) {
    perror("Problem sending data");
    exit(1);
  }

  if (n_sent!=buf_len) {
    printf("Sendto sent %d bytes\n",n_sent);
  }

  /* Wait for a reply (from anyone) */
  n_read = recvfrom(sk,buf,MAXBUF,0,NULL,NULL);
  if (n_read<0) {
    perror("Problem in recvfrom");
    exit(1);
  }

  printf("Got back %d bytes\n",n_read);
  /* send what we got back to stdout */
  if (write(STDOUT_FILENO,buf,n_read) < 0) {
    perror("Problem writing to stdout");
    exit(1);
  }
  return(0);
}
