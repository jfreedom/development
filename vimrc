syntax on
set autoindent
set ruler
set incsearch
:filetype indent on
set nowrap
set tabstop=2
set shiftwidth=2
set expandtab
nnoremap <CR> :noh<CR><CR>
set columns=120
set lines=53
colorscheme desert
set diffopt="iwhite,filler"
set noerrorbells visualbell t_vb=
autocmd GUIEnter * set visualbell t_vb=

"Auto complete
imap <C-Space> <C-P>
imap <C-k> <C-P>
imap <C-j> <C-N>
set complete=.,b,u,]
set wildmode=longest,list:longest
set completeopt=menu,preview

augroup vimrc_filetype
  autocmd!
  autocmd FileType python call s:MyPySettings()
  autocmd Filetype c call s:MyCSettings()
  autocmd FileType cpp call s:MyCSettings()
  autocmd FileType bash,sh call s:MyBashSettings()
  autocmd Filetype make call s:MyMakeSettings()
augroup end

function! s:MyPySettings()
  set tabstop=4
  set shiftwidth=4
  set noexpandtab
  map - :s/^/\#/<CR>:nohlsearch<CR>
  map + : '<,'>g/^/norm I## <CR>
endfunction

function! s:MyCSettings()
  set tabstop=2
  set shiftwidth=2
  map - ?[{#][^p]<CR>zf%<CR>:noh<CR>k
  map + zo
endfunction  

function! s:MyBashSettings()
  map - j?function.*{<CR>zf%<CR>
  map + zo
endfunction

function! s:MyMakeSettings()
  set noexpandtab
  set tabstop=4
endfunction
