
# shell options
shopt -s cdable_vars
shopt -s cmdhist
export PATH=/home/jef/bin:$PATH
alias vi=gvim
export PS1="\[\033[36m\]\u\[\033[m\]@\[\033[32m\]\h:\[\033[33;1m\]\W\[\033[m\]\$ "
#export PS1='[\u@\h \W]\$ '
alias ls='ls -F --color'
alias cls='clear'


function test {
  echo $@
}
