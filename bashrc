# shell options
shopt -s cdable_vars
shopt -s cmdhist
shopt -s histappend


#history
export PROMPT_COMMAND="history -a; history -c; history -r; $PROMPT_COMMAND"
export HISTFILESIZE=100000
export HISTCONTROL=ignoredups:erasedups

#path
export PATH=$PATH:$HOME/bin/ssh
export PATH=$PATH:$HOME/bin


alias vi=gvim
alias ls='ls -F --color'
alias cls='clear'

#project specific bashrcs
for i in ~/development/*; do
  if [ -e $i/.bashrc ]; then
    source $i/.bashrc
  fi
done

#Prompt
export TERM=xterm-256color
BLACK=$(tput setaf 0)
RED=$(tput setaf 1)
GREEN=$(tput setaf 2)
LIME_YELLOW=$(tput setaf 190)
YELLOW=$(tput setaf 3)
POWDER_BLUE=$(tput setaf 153)
BLUE=$(tput setaf 4)
MAGENTA=$(tput setaf 5)
CYAN=$(tput setaf 6)
WHITE=$(tput setaf 7)
BRIGHT=$(tput bold)
NORMAL=$(tput sgr0)
BLINK=$(tput blink)
REVERSE=$(tput smso)
UNDERLINE=$(tput smul)
export PS1="\[${CYAN}\]\u\[${NORMAL}\]@\[${YELLOW}\]\h:\[${NORMAL}\]\W\$ "
