#include <unistd.h>
#include <ncurses.h>
#include <ctype.h>
#include <stdlib.h>
#include <sys/time.h>
#include <signal.h>
#include <sstream>
#include <cstring>

#include "io.h"
#include "move.h"
#include "path.h"
#include "pc.h"
#include "utils.h"
#include "dungeon.h"
#include "descriptions.h"

/* Same ugly hack we did in path.c */
static dungeon_t *dungeon;

typedef struct io_message {
  /* Will print " --more-- " at end of line when another message follows. *
   * Leave 10 extra spaces for that.                                      */
  char msg[500];
  struct io_message *next;
} io_message_t;

static io_message_t *io_head, *io_tail;
/*
static void sigalrm_handler(int unused)
{
  io_display(dungeon);
}

static void mask_alarm(void)
{
  sigset_t s;

  sigemptyset(&s);
  sigaddset(&s, SIGALRM);
  sigprocmask(SIG_BLOCK, &s, NULL);
}

static void unmask_alarm(void)
{
  sigset_t s;

  sigemptyset(&s);
  sigaddset(&s, SIGALRM);
  sigprocmask(SIG_UNBLOCK, &s, NULL);
}
*/
void io_init_terminal(dungeon_t *d)
{
  //struct itimerval itv;

  initscr();
  raw();
  noecho();
  curs_set(0);
  keypad(stdscr, TRUE);
  start_color();
  init_pair(COLOR_RED, COLOR_RED, COLOR_BLACK);
  init_pair(COLOR_GREEN, COLOR_GREEN, COLOR_BLACK);
  init_pair(COLOR_YELLOW, COLOR_YELLOW, COLOR_BLACK);
  init_pair(COLOR_BLUE, COLOR_BLUE, COLOR_BLACK);
  init_pair(COLOR_MAGENTA, COLOR_MAGENTA, COLOR_BLACK);
  init_pair(COLOR_CYAN, COLOR_CYAN, COLOR_BLACK);
  init_pair(COLOR_WHITE, COLOR_WHITE, COLOR_BLACK);
  /*
  itv.it_interval.tv_sec = 0;
  itv.it_interval.tv_usec = 200000; 
  itv.it_value.tv_sec = 0;
  itv.it_value.tv_usec = 200000;
  setitimer(ITIMER_REAL, &itv, NULL);
  dungeon = d;
  signal(SIGALRM, sigalrm_handler);
  */
}

void io_reset_terminal(void)
{
  endwin();

  while (io_head) {
    io_tail = io_head;
    io_head = io_head->next;
    free(io_tail);
  }
  io_tail = NULL;
}

void io_queue_message(const char *format, ...)
{
  io_message_t *tmp;
  va_list ap;

  if (!(tmp = (io_message_t *) malloc(sizeof (*tmp)))) {
    perror("malloc");
    exit(1);
  }

  tmp->next = NULL;

  va_start(ap, format);

  vsnprintf(tmp->msg, sizeof (tmp->msg), format, ap);

  va_end(ap);

  if (!io_head) {
    io_head = io_tail = tmp;
  } else {
    io_tail->next = tmp;
    io_tail = tmp;
  }
}

static void io_print_message_queue(uint32_t y, uint32_t x)
{
  //mask_alarm();
  while (io_head) {
    io_tail = io_head;
    attron(COLOR_PAIR(COLOR_CYAN));
    mvprintw(y, x, "%-80s", io_head->msg);
    attroff(COLOR_PAIR(COLOR_CYAN));
    io_head = io_head->next;
    if (io_head) {
      attron(COLOR_PAIR(COLOR_CYAN));
      mvprintw(y, x + 78, "%10s", " --more-- ");
      attroff(COLOR_PAIR(COLOR_CYAN));
      refresh();
      getch();
    }
    free(io_tail);
  }
  io_tail = NULL;
  //unmask_alarm();
}

static char distance_to_char[] = {
  '0',
  '1',
  '2',
  '3',
  '4',
  '5',
  '6',
  '7',
  '8',
  '9',
  'a',
  'b',
  'c',
  'd',
  'e',
  'f',
  'g',
  'h',
  'i',
  'j',
  'k',
  'l',
  'm',
  'n',
  'o',
  'p',
  'q',
  'r',
  's',
  't',
  'u',
  'v',
  'w',
  'x',
  'y',
  'z',
  'A',
  'B',
  'C',
  'D',
  'E',
  'F',
  'G',
  'H',
  'I',
  'J',
  'K',
  'L',
  'M',
  'N',
  'O',
  'P',
  'Q',
  'R',
  'S',
  'T',
  'U',
  'V',
  'W',
  'X',
  'Y',
  'Z'
};

void io_display_tunnel(dungeon_t *d)
{
  uint32_t y, x;
  //mask_alarm();
  clear();
  for (y = 0; y < DUNGEON_Y; y++) {
    for (x = 0; x < DUNGEON_X; x++) {
      mvaddch(y + 1, x, (d->pc_tunnel[y][x] < 62              ?
                         distance_to_char[d->pc_tunnel[y][x]] :
                         '*'));
    }
  }
  refresh();
  while (getch() != 27 /* ESC */)
    ;
  //unmask_alarm();
}

void io_display_distance(dungeon_t *d)
{
  uint32_t y, x;
  //mask_alarm();
  clear();
  for (y = 0; y < DUNGEON_Y; y++) {
    for (x = 0; x < DUNGEON_X; x++) {
      mvaddch(y + 1, x, (d->pc_distance[y][x] < 62              ?
                         distance_to_char[d->pc_distance[y][x]] :
                         '*'));
    }
  }
  refresh();
  while (getch() != 27 /* ESC */)
    ;
  //unmask_alarm();
}

void io_display_hardness(dungeon_t *d)
{
  uint32_t y, x;
  //mask_alarm();
  clear();
  for (y = 0; y < DUNGEON_Y; y++) {
    for (x = 0; x < DUNGEON_X; x++) {
      /* Maximum hardness is 255.  We have 62 values to display it, but *
       * we only want one zero value, so we need to cover [1,255] with  *
       * 61 values, which gives us a divisor of 254 / 61 = 4.164.       *
       * Generally, we want to avoid floating point math, but this is   *
       * not gameplay, so we'll make an exception here to get maximal   *
       * hardness display resolution.                                   */
      mvaddch(y + 1, x, (d->hardness[y][x]                             ?
                         distance_to_char[1 + (d->hardness[y][x] / 5)] :
                         '0'));
    }
  }
  refresh();
  while (getch() != 27 /* ESC */)
    ;
  //unmask_alarm();
}

void io_display_all(dungeon_t *d)
{
  uint32_t y, x;

  //mask_alarm();
  clear();
  for (y = 0; y < DUNGEON_Y; y++) {
    for (x = 0; x < DUNGEON_X; x++) {
      if (d->charmap[y][x]) {
        attron(COLOR_PAIR(d->charmap[y][x]->get_color()));
        mvaddch(y + 1, x, d->charmap[y][x]->get_symbol());
        attroff(COLOR_PAIR(d->charmap[y][x]->get_color()));
      } else if (d->objmap[y][x] && d->objmap[y][x]->have_seen()) {
        attron(COLOR_PAIR(d->objmap[y][x]->get_color()));
        mvaddch(y + 1, x, d->objmap[y][x]->get_symbol());
        attroff(COLOR_PAIR(d->objmap[y][x]->get_color()));
      } else {
        switch (mapxy(x, y)) {
        case ter_wall:
        case ter_wall_immutable:
          mvaddch(y + 1, x, ' ');
          break;
        case ter_floor:
        case ter_floor_room:
          mvaddch(y + 1, x, '.');
          break;
        case ter_floor_hall:
          mvaddch(y + 1, x, '#');
          break;
        case ter_debug:
          mvaddch(y + 1, x, '*');
          break;
        case ter_stairs_up:
          mvaddch(y + 1, x, '<');
          break;
        case ter_stairs_down:
          mvaddch(y + 1, x, '>');
          break;
        default:
 /* Use zero as an error symbol, since it stands out somewhat, and it's *
  * not otherwise used.                                                 */
          mvaddch(y + 1, x, '0');
        }
      }
    }
  }

  io_print_message_queue(0, 0);

  refresh();
  //unmask_alarm();
}

void io_display(dungeon_t *d)
{
  uint32_t y, x;
  uint32_t illuminated;
  clear();
  for (y = 0; y < DUNGEON_Y; y++) {
    for (x = 0; x < DUNGEON_X; x++) {
      if ((illuminated = is_illuminated(d->pc, y, x))) {
        attron(A_BOLD);
      }
      if (d->charmap[y][x] &&
          can_see(d,
                  character_get_pos(d->pc),
                  character_get_pos(d->charmap[y][x]),
                  1)) {
        attron(COLOR_PAIR(d->charmap[y][x]->get_color()));
        mvaddch(y + 1, x, d->charmap[y][x]->get_symbol());
        attroff(COLOR_PAIR(d->charmap[y][x]->get_color()));
      } else if (d->objmap[y][x] && d->objmap[y][x]->have_seen()) {
        attron(COLOR_PAIR(d->objmap[y][x]->get_color()));
        mvaddch(y + 1, x, d->objmap[y][x]->get_symbol());
        attroff(COLOR_PAIR(d->objmap[y][x]->get_color()));
      } else {
        switch (pc_learned_terrain(d->pc, y, x)) {
        case ter_wall:
        case ter_wall_immutable:
        case ter_unknown:
          mvaddch(y + 1, x, ' ');
          break;
        case ter_floor:
        case ter_floor_room:
          mvaddch(y + 1, x, '.');
          break;
        case ter_floor_hall:
          mvaddch(y + 1, x, '#');
          break;
        case ter_debug:
          mvaddch(y + 1, x, '*');
          break;
        case ter_stairs_up:
          mvaddch(y + 1, x, '<');
          break;
        case ter_stairs_down:
          mvaddch(y + 1, x, '>');
          break;
        default:
 /* Use zero as an error symbol, since it stands out somewhat, and it's *
  * not otherwise used.                                                 */
          mvaddch(y + 1, x, '0');
        }
      }
      if (illuminated) {
        attroff(A_BOLD);
      }
    }
  }

  io_print_message_queue(0, 0);

  refresh();
}

void io_display_monster_list(dungeon_t *d)
{
  mvprintw(11, 33, " HP:    XXXXX ");
  mvprintw(12, 33, " Speed: XXXXX ");
  mvprintw(14, 27, " Hit any key to continue. ");
  refresh();
  getch();
}

uint32_t io_teleport_pc(dungeon_t *d)
{
  /* Just for fun. */
  pair_t dest;

  do {
    dest[dim_x] = rand_range(1, DUNGEON_X - 2);
    dest[dim_y] = rand_range(1, DUNGEON_Y - 2);
  } while (charpair(dest));

  d->charmap[character_get_y(d->pc)][character_get_x(d->pc)] = NULL;
  d->charmap[dest[dim_y]][dest[dim_x]] = d->pc;

  character_set_y(d->pc, dest[dim_y]);
  character_set_x(d->pc, dest[dim_x]);

  if (mappair(dest) < ter_floor) {
    mappair(dest) = ter_floor;
  }

  pc_observe_terrain(d->pc, d);

  dijkstra(d);
  dijkstra_tunnel(d);

  return 0;
}

/* Adjectives to describe our monsters */
static const char *adjectives[] = {
  "A menacing ",
  "A threatening ",
  "A horrifying ",
  "An intimidating ",
  "An aggressive ",
  "A frightening ",
  "A terrifying ",
  "A terrorizing ",
  "An alarming ",
  "A frightening ",
  "A dangerous ",
  "A glowering ",
  "A glaring ",
  "A scowling ",
  "A chilling ",
  "A scary ",
  "A creepy ",
  "An eerie ",
  "A spooky ",
  "A slobbering ",
  "A drooling ",
  " A horrendous ",
  "An unnerving ",
  "A cute little ",  /* Even though they're trying to kill you, */
  "A teeny-weenie ", /* they can still be cute!                 */
  "A fuzzy ",
  "A fluffy white ",
  "A kawaii ",       /* For our otaku */
  "Hao ke ai de "    /* And for our Chinese */
  /* And there's one special case (see below) */
};

static void io_scroll_monster_list(char (*s)[40], uint32_t count)
{
  uint32_t offset;
  uint32_t i;

  offset = 0;

  while (1) {
    for (i = 0; i < 13; i++) {
      mvprintw(i + 6, 19, " %-40s ", s[i + offset]);
    }
    switch (getch()) {
    case KEY_UP:
      if (offset) {
        offset--;
      }
      break;
    case KEY_DOWN:
      if (offset < (count - 13)) {
        offset++;
      }
      break;
    case 27:
      return;
    }

  }
}

static bool is_vowel(const char c)
{
  return (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' ||
          c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U');
}

static void io_list_monsters_display(dungeon_t *d,
                                     character **c,
                                     uint32_t count)
{
  uint32_t i;
  char (*s)[40]; /* pointer to array of 40 char */

  (void) adjectives;

  s = (char (*)[40]) malloc(count * sizeof (*s));

  mvprintw(3, 19, " %-40s ", "");
  /* Borrow the first element of our array for this string: */
  snprintf(s[0], 40, "You know of %d monsters:", count);
  mvprintw(4, 19, " %-40s ", s);
  mvprintw(5, 19, " %-40s ", "");

  for (i = 0; i < count; i++) {
    snprintf(s[i], 40, "%3s%s (%c): %2d %s by %2d %s",
             (is_vowel(character_get_name(c[i])[0]) ? "An " : "A "),
             character_get_name(c[i]),
             character_get_symbol(c[i]),
             abs(character_get_y(c[i]) - character_get_y(d->pc)),
             ((character_get_y(c[i]) - character_get_y(d->pc)) <= 0 ?
              "North" : "South"),
             abs(character_get_x(c[i]) - character_get_x(d->pc)),
             ((character_get_x(c[i]) - character_get_x(d->pc)) <= 0 ?
              "East" : "West"));
    if (count <= 13) {
      /* Handle the non-scrolling case right here. *
       * Scrolling in another function.            */
      mvprintw(i + 6, 19, " %-40s ", s[i]);
    }
  }

  if (count <= 13) {
    mvprintw(count + 6, 19, " %-40s ", "");
    mvprintw(count + 7, 19, " %-40s ", "Hit escape to continue.");
    while (getch() != 27 /* escape */)
      ;
  } else {
    mvprintw(19, 19, " %-40s ", "");
    mvprintw(20, 19, " %-40s ",
             "Arrows to scroll, escape to continue.");
    io_scroll_monster_list(s, count);
  }

  free(s);
}

static int compare_monster_distance(const void *v1, const void *v2)
{
  const character *const *c1 = (const character * const *) v1;
  const character *const *c2 = (const character * const *) v2;

  return (dungeon->pc_distance[character_get_y(*c1)][character_get_x(*c1)] -
          dungeon->pc_distance[character_get_y(*c2)][character_get_x(*c2)]);
}

static void io_list_monsters(dungeon_t *d)
{
  character **c;
  uint32_t x, y, count;

  //mask_alarm();
  c = (character **) malloc(d->num_monsters * sizeof (*c));

  /* Get a linear list of monsters */
  for (count = 0, y = 1; y < DUNGEON_Y - 1; y++) {
    for (x = 1; x < DUNGEON_X - 1; x++) {
      if (d->charmap[y][x] &&
          d->charmap[y][x] != d->pc &&
          can_see(d, d->pc->position, character_get_pos(d->charmap[y][x]), 1)) {
        c[count++] = d->charmap[y][x];
      }
    }
  }
  /* Sort it by distance from PC */
  dungeon = d;
  qsort(c, count, sizeof (*c), compare_monster_distance);

  /* Display it */
  io_list_monsters_display(d, c, count);
  free(c);

  //unmask_alarm();

  /* And redraw the dungeon */
  io_display(d);
}

static const int equip_num[] = {
  'a',
  'b',
  'c',
  'd',
  'e',
  'f',
  'g',
  'h',
  'i',
  'j',
  'k',
  'l'
};

#define type_lu_entry(type) { #type, objtype_##type }
static const struct {
  const char* name;
  const object_type_t value;
} object_type[] = {
  type_lu_entry(WEAPON),
  type_lu_entry(OFFHAND), 
  type_lu_entry(RANGED), 
  type_lu_entry(ARMOR),
  type_lu_entry(HELMET),
  type_lu_entry(CLOAK),
  type_lu_entry(GLOVES),
  type_lu_entry(BOOTS),
  type_lu_entry(AMULET),
  type_lu_entry(LIGHT),
  type_lu_entry(RING),
  type_lu_entry(RING)
};

static void io_list_pc_inventory(dungeon_t *d) {
  uint32_t i;
  int y = 4;

  //mask_alarm();

  clear();
  io_display(d);
  
  mvprintw(3, 25, "%s", "         PC INVENTORY         ");
  for (i = 0; i < 10; i++) {
    mvprintw(y, 25, "%s", "                              ");
    mvprintw(y, 27, "%d", i);
    mvprintw(y, 28, "%c", '.');
    if (((pc *)(d->pc))->carry_slot[i] == NULL) {
      mvprintw(y, 30, "%s", "EMPTY");
    }
    else {
      mvprintw(y, 30, "%s", ((pc *)(d->pc))->carry_slot[i]->get_name());
    } 
    y++;
  }
  //unmask_alarm();
}

static void io_list_pc_equipment(dungeon_t *d) {
  uint32_t i;
  int y = 4;
  
  //mask_alarm();

  clear();
  io_display(d);
  
  mvprintw(3, 25, "%s", "         PC EQUIPMENT         ");
  for (i = 0; i < 12; i++) {
    mvprintw(y, 25, "%s", "                              ");
    mvprintw(y, 27, "%c", equip_num[i]);
    mvprintw(y, 28, "%c", '.');
    mvprintw(y, 30, "%s", object_type[i].name);
    if (((pc *)(d->pc))->equip_slot[i] == NULL) {
      mvprintw(y, 40, "%s", "EMPTY");
    }
    else {
      mvprintw(y, 40, "%s", ((pc *)(d->pc))->equip_slot[i]->get_name());
    }
    y++;
  }
  //unmask_alarm();
}

static void wear_item(dungeon_t *d) {
  int item, i = 0;
  object *obj;
  std::stringstream status;
  const char *name;
  io_queue_message("Select an item to wear");
  io_queue_message("Hit escape to exit.");
  io_list_pc_inventory(d);
  item = getch();
  if (item == 27) {
    clear();
    io_display(d);
    return;
  }
  item = item - 48;
  if ((item > 10) || (item < 10 && ((pc *)(d->pc))->carry_slot[item] == NULL)) {
    io_queue_message( "Invalid item number, please try again.");
    clear();
    io_display(d);
    return;
  }
  clear();
  io_display(d);
  name = ((pc *)(d->pc))->carry_slot[item]->get_name();
  for (i = 0; i < 12 && ((pc *)(d->pc))->carry_slot[item] != NULL; i++) {
    if (((pc *)(d->pc))->carry_slot[item]->get_type() == object_type[i].value) {
      if (((pc *)(d->pc))->equip_slot[i] == NULL) {
	((pc *)(d->pc))->equip_slot[i] = ((pc *)(d->pc))->carry_slot[item];
	((pc *)(d->pc))->carry_slot[item] = NULL;
	//update stats
	d->pc->speed += ((pc *)(d->pc))->equip_slot[i]->get_speed();
      }
      else {
	obj = ((pc *)(d->pc))->equip_slot[i];
	((pc *)(d->pc))->equip_slot[i] = ((pc *)(d->pc))->carry_slot[item];
	((pc *)(d->pc))->carry_slot[item] = obj;
	//update stats
	d->pc->speed += (((pc *)(d->pc))->equip_slot[i]->get_speed() - ((pc *)(d->pc))->carry_slot[item]->get_speed());
	if (d->pc->speed <= 0) { d->pc->speed = 1;}
      }
    }
  }
  status << "Item Worn: " << name;
  io_queue_message(status.str().c_str());
  io_display(d);
}

static void take_off_item(dungeon_t *d) {
  int item, j;
  std::stringstream status;
  const char *name;
  io_queue_message("Select an item to take off:");
  io_queue_message("Hit escape to exit.");
  io_list_pc_equipment(d);
  item = getch();
  if (item == 27) {
    clear();
    io_display(d);
    return;
  }
  item = item - 97;
  if ((item > 12) || (item < 12 && ((pc *)(d->pc))->equip_slot[item] == NULL)) {
    io_queue_message( "Invalid item number, please try again.");
    clear();
    io_display(d);
    return;
  }
  clear();
  io_display(d);
  name = ((pc *)(d->pc))->equip_slot[item]->get_name();
  //find open carry slot
  for (j = 0; j < 10 && ((pc *)(d->pc))->carry_slot[j] != NULL; j++) {}
  if (j == 10) {
    io_queue_message("There is no room in the inventory, please drop an item first");
    clear();
    io_display(d);
    return;
  }
  else {
  //take off item 
    ((pc *)(d->pc))->carry_slot[j] = ((pc *)(d->pc))->equip_slot[item];
    //update stats
    d->pc->speed -= ((pc *)(d->pc))->equip_slot[item]->get_speed();
    if (d->pc->speed <= 0) { d->pc->speed = 1;}
    ((pc *)(d->pc))->equip_slot[item] = NULL;
  }
  status << "Item Taken Off: " << name; 
  io_queue_message(status.str().c_str());
  io_display(d);
}   

static void drop_item(dungeon_t *d) {
  int item;
  std::stringstream status;
  const char *name;
  io_queue_message("Select an item to drop:");
  io_queue_message("Hit escape to exit.");
  io_list_pc_inventory(d);
  item = getch();
  if (item == 27) {
    clear();
    io_display(d);
    return;
  }
  item = item - 48;
  if ((item > 10) || (item < 10 && ((pc *)(d->pc))->carry_slot[item] == NULL)) {
    io_queue_message( "Invalid item number, please try again.");
    clear();
    io_display(d);
    return;
  }
  clear();
  io_display(d);
  name = ((pc *)(d->pc))->carry_slot[item]->get_name();
  d->objmap[d->pc->position[dim_y]][d->pc->position[dim_x]] = ((pc *)(d->pc))->carry_slot[item];
  ((pc *)(d->pc))->carry_slot[item] = NULL;
  status << "Item Dropped: " << name;
  io_queue_message(status.str().c_str());
  io_display(d);
}

static void expunge_item(dungeon_t *d) {
  int item;
  std::stringstream status;
  const char *name;
  io_queue_message("Select an item to destroy");
  io_queue_message("Hit escape to exit.");
  io_list_pc_inventory(d);
  item = getch();
  if (item == 27) {
    clear();
    io_display(d);
    return;
  }
  item = item - 48;
  if ((item > 10) || (item < 10 && ((pc *)(d->pc))->carry_slot[item] == NULL)) {
    io_queue_message( "Invalid item number, please try again.");
    clear();
    io_display(d);
    return;
  }
  clear();
  io_display(d);
  name = ((pc *)(d->pc))->carry_slot[item]->get_name();
  delete ((pc *)(d->pc))->carry_slot[item];
  ((pc *)(d->pc))->carry_slot[item] = NULL;
  status << "Item Destroyed: " << name;
  io_queue_message(status.str().c_str());
  io_display(d);
}

static void inspect_item(dungeon_t *d) {
  int item;
  std::stringstream desc;
  io_queue_message("Select an item to inspect");
  io_queue_message("Hit escape to exit.");
  io_list_pc_inventory(d);
  item = getch();
  if (item == 27) {
    clear();
    io_display(d);
    return;
  }
  item = item - 48;
  if ((item > 10) || (item < 10 && ((pc *)(d->pc))->carry_slot[item] == NULL)) {
    io_queue_message( "Invalid item number, please try again.");
    clear();
    io_display(d);
    return;
  }
  clear();
  io_queue_message(((pc *)(d->pc))->carry_slot[item]->get_name());
  io_queue_message(((pc *)(d->pc))->carry_slot[item]->get_description());
  io_display(d);
}

void io_handle_input(dungeon_t *d)
{
  uint32_t fail_code;
  int key;

  do {
    switch (key = getch()) {
    case '7':
    case 'y':
    case KEY_HOME:
      fail_code = move_pc(d, 7);
      break;
    case '8':
    case 'k':
    case KEY_UP:
      fail_code = move_pc(d, 8);
      break;
    case '9':
    case 'u':
    case KEY_PPAGE:
      fail_code = move_pc(d, 9);
      break;
    case '6':
    case 'l':
    case KEY_RIGHT:
      fail_code = move_pc(d, 6);
      break;
    case '3':
    case 'n':
    case KEY_NPAGE:
      fail_code = move_pc(d, 3);
      break;
    case '2':
    case 'j':
    case KEY_DOWN:
      fail_code = move_pc(d, 2);
      break;
    case '1':
    case 'b':
    case KEY_END:
      fail_code = move_pc(d, 1);
      break;
    case '4':
    case 'h':
    case KEY_LEFT:
      fail_code = move_pc(d, 4);
      break;
    case '5':
    case ' ':
    case KEY_B2:
      fail_code = 0;
      break;
    case '>':
      fail_code = move_pc(d, '>');
      break;
    case '<':
      fail_code = move_pc(d, '<');
      break;
    //ranged attack
    case 'r':
      fail_code = ranged_attack(d);
      break;
    case 'w':
      wear_item(d);
      fail_code = 1;
      break;
    case 't':
      take_off_item(d);
      fail_code = 1;
      break;
    case 'd':
      drop_item(d);
      fail_code = 1;
      break;
    case 'x':
      expunge_item(d);
      fail_code = 1;
      break;
    case 'S':
      d->save_and_exit = 1;
      character_reset_turn(d->pc);
      fail_code = 0;
      break;
    case 'Q':
      /* Extra command, not in the spec.  Quit without saving.          */
      d->quit_no_save = 1;
      fail_code = 0;
      break;
    case 'T':
      /* New command.  Display the distances for tunnelers.             */
      io_display_tunnel(d);
      fail_code = 1;
      break;
    case 'D':
      /* New command.  Display the distances for non-tunnelers.         */
      io_display_distance(d);
      fail_code = 1;
      break;
    case 'H':
      /* New command.  Display the hardnesses.                          */
      io_display_hardness(d);
      fail_code = 1;
      break;
    case 's':
      /* New command.  Return to normal display after displaying some   *
       * special screen.                                                */
      io_display(d);
      fail_code = 1;
      break;
    case 'g':
      /* Teleport the PC to a random place in the dungeon.              */
      io_teleport_pc(d);
      fail_code = 0;
      break;
    case 'i':
      io_queue_message("Hit escape to exit");
      io_list_pc_inventory(d);
      while (getch() != 27) {}
      io_display(d);
      fail_code = 1;
      break;
    case 'e':
      io_queue_message("Hit escape to exit");
      io_list_pc_equipment(d);
      while (getch() != 27) {}
      io_display(d);
      fail_code = 1;
      break;
    case 'I':
      inspect_item(d);
      fail_code = 1;
      break;
    case 'm':
      io_list_monsters(d);
      fail_code = 1;
      break;
    case 'a':
      io_display_all(d);
      fail_code = 1;
      break;
    case 'q':
      /* Demonstrate use of the message queue.  You can use this for *
       * printf()-style debugging (though gdb is probably a better   *
       * option.  Not that it matterrs, but using this command will  *
       * waste a turn.  Set fail_code to 1 and you should be able to *
       * figure out why I did it that way.                           */
      io_queue_message("This is the first message.");
      io_queue_message("Since there are multiple messages, "
                       "you will see \"more\" prompts.");
      io_queue_message("You can use any key to advance through messages.");
      io_queue_message("Normal gameplay will not resume until the queue "
                       "is empty.");
      io_queue_message("Long lines will be truncated, not wrapped.");
      io_queue_message("io_queue_message() is variadic and handles "
                       "all printf() conversion specifiers.");
      io_queue_message("Did you see %s?", "what I did there");
      io_queue_message("When the last message is displayed, there will "
                       "be no \"more\" prompt.");
      io_queue_message("Have fun!  And happy printing!");
      fail_code = 0;
      break;
    default:
      /* Also not in the spec.  It's not always easy to figure out what *
       * key code corresponds with a given keystroke.  Print out any    *
       * unhandled key here.  Not only does it give a visual error      *
       * indicator, but it also gives an integer value that can be used *
       * for that key in this (or other) switch statements.  Printed in *
       * octal, with the leading zero, because ncurses.h lists codes in *
       * octal, thus allowing us to do reverse lookups.  If a key has a *
       * name defined in the header, you can use the name here, else    *
       * you can directly use the octal value.                          */
      mvprintw(0, 0, "Unbound key: %#o ", key);
      fail_code = 1;
    }
  } while (fail_code);
}
