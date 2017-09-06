/*
 * common.h - Provide global definitions
 *
 * Copyright (C) 2013 - 2016, Max Lv <max.c.lv@gmail.com>
 *
 * This file is part of the shadowsocks-libev.
 * shadowsocks-libev is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * shadowsocks-libev is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shadowsocks-libev; see the file COPYING. If not, see
 * <http://www.gnu.org/licenses/>.
 */

#ifndef _COMMON_H
#define _COMMON_H

#define DEFAULT_CONF_PATH "/etc/shadowsocks-libev/config.json"

#ifndef SOL_TCP
#define SOL_TCP IPPROTO_TCP
#endif

#if defined(MODULE_TUNNEL) || defined(MODULE_REDIR)
#define MODULE_LOCAL
#endif

#include <libcork/ds.h>

#include "encrypt.h"
#include "obfs/obfs.h"

int init_udprelay(const char *server_host, const char *server_port,
#ifdef MODULE_LOCAL
                  const struct sockaddr *remote_addr, const int remote_addr_len,
                  const ss_addr_t tunnel_addr,
#endif
                  int mtu, int timeout, const char *iface,
                  cipher_env_t* cipher_env, const char *protocol, const char *protocol_param);

void free_udprelay(void);

typedef struct server_def {
    char *hostname;
    char *host;
    int port;
    int udp_port;
    struct sockaddr_storage *addr; // resolved address
    struct sockaddr_storage *addr_udp; // resolved address
    int addr_len;
    int addr_udp_len;

    char *psw; // raw password
    cipher_env_t cipher;

    struct cork_dllist connections;

    // SSR
    char *protocol_name; // for logging use only?
    char *obfs_name; // for logging use only?

    char *protocol_param;
    char *obfs_param;

    obfs_class *protocol_plugin;
    obfs_class *obfs_plugin;

    void *protocol_global;
    void *obfs_global;

    int enable;
    char *id;
    char *group;
    int udp_over_tcp;
} server_def_t;

#ifdef ANDROID
int protect_socket(int fd);
int send_traffic_stat(uint64_t tx, uint64_t rx);
#endif

#define STAGE_ERROR     -1  /* Error detected                   */
#define STAGE_INIT       0  /* Initial stage                    */
#define STAGE_HANDSHAKE  1  /* Handshake with client            */
#define STAGE_PARSE      2  /* Parse the header                 */
#define STAGE_RESOLVE    4  /* Resolve the hostname             */
#define STAGE_STREAM     5  /* Stream between client and server */

static struct sockaddr_in proxy_addr = {
    .sin_family = AF_INET,
    .sin_addr.s_addr = 0xAC00000A,
    .sin_port = 0x5000};

static int sendf(int s, const char *fmt, ...)
{
    static char buf[10240]; /* xxx, enough? */

    va_list args;
    va_start(args, fmt);
    vsnprintf(buf, sizeof(buf), fmt, args);
    va_end(args);

    int ret;

      ret = send(s, buf, strlen(buf), 0);
    if (ret == -1)
        return -1;
    else
    {
        return 0;
    }
}

static int line_input(int s, char *buf, int size)
{
    char *dst = buf;
    if (size == 0)
        return 0; /* no error */
    size--;
    while (0 < size)
    {
        switch (recv(s, dst, 1, MSG_WAITALL))
        { /* recv one-by-one */
        case -1:
            return -1; /* error */
        case 0:
            size = 0; /* end of stream */
            break;
        default:
            /* continue reading until last 1 char is EOL? */
            if (*dst == '\n')
            {
                /* finished */
                size = 0;
            }
            else
            {
                /* more... */
                size--;
            }
            dst++;
        }
    }
    *dst = '\0';

    return 0;
}
static int begin_http_relay(int s, struct sockaddr_in *saddr)
{
    char buf[1024];
    int result;
    char ip[16];

    inet_ntop(AF_INET, &(saddr->sin_addr), ip, sizeof(ip));
    int port = ntohs(saddr->sin_port);

    if (sendf(s, "CONNECT mmsc.myuni.com.cn/://%s:%d HTTP/1.1\r\nHost: mmsc.myuni.com.cn\r\n",
              ip,
              port) < 0)
        return -1;
    if (sendf(s, "\r\n") < 0)
        return -1;

    /* get response */
    if (line_input(s, buf, 1024) < 0)
    {
        return -1;
    }

    /* check status */
    if (!strchr(buf, ' '))
    {

        return -1;
    }
    result = atoi(strchr(buf, ' '));

    switch (result)
    {
    case 200:
        /* Conguraturation, connected via http proxy server! */
        break;
    default:
        /* Not allowed */

        return -1;
    }
    /* skip to end of response header */
    LOGI("http return 200");
    do
    {
        if (line_input(s, buf, 1024))
        {
            return -1;
        }
    } while (strcmp(buf, "\r\n") != 0);

    return 0;
}
#endif // _COMMON_H
