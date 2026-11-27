# Мультяшний стиль розмальовки: контур + заливка кольором фону, кінцівки-«трубки», голови hb/hg
import math
L='LINE'; B='BG'
def f(d): return f'<path class="f" d="{d}"/>'
def ln(d,w=2): return f'<path d="{d}" stroke-width="{w}"/>' if w!=2 else f'<path d="{d}"/>'
def circ(cx,cy,r,fill=True): return f'<circle {"class=\"f\" " if fill else ""}cx="{cx}" cy="{cy}" r="{r}"/>'
def rect(x,y,w,h,rx=0,fill=True): return f'<rect {"class=\"f\" " if fill else ""}x="{x}" y="{y}" width="{w}" height="{h}" rx="{rx}"/>'
def tube(pts,ow=7,iw=3.4):
    d='M'+' L'.join(f'{x:.1f} {y:.1f}' for x,y in pts)
    return f'<path d="{d}" stroke-width="{ow}"/><path d="{d}" class="in" stroke-width="{iw}"/>'
def hand(x,y,r=2.6): return f'<circle class="f" cx="{x}" cy="{y}" r="{r}"/>'
def shoe(x,y,d=1): return f'<path class="f" d="M{x-3*d} {y-2.5}h{5*d}q{4*d} 0 {4*d} 3v1h-{9*d}z"/>'
def head(x,y,g='b',s=1,flip=False): return f'<use href="#h{g}" transform="translate({x} {y}) scale({-s if flip else s} {s})"/>'
def person(j,g='b',s=1,shirt='t',shorts=True,hat=None,flip=False):
    """j: neck,hip,eL,hL,eR,hR,kL,fL,kR,fR (L — задній план). Масштаб s застосовується до товщини."""
    o=''
    ow,iw=6.5*s,3.2*s; low,liw=7.5*s,3.8*s
    # задні кінцівки
    o+=tube([j['hip'],j['kL'],j['fL']],low,liw)+shoe(*j['fL'],1 if j['fL'][0]>=j['kL'][0] else -1)
    o+=tube([j['neck'],j['eL'],j['hL']],ow,iw)+hand(*j['hL'],2.6*s)
    # тулуб
    nx,ny=j['neck']; hx,hy=j['hip']; a=math.atan2(hy-ny,hx-nx); px,py=-math.sin(a),math.cos(a)
    w1,w2=7.5*s,6*s
    pts=[(nx+px*w1,ny+py*w1),(hx+px*w2,hy+py*w2),(hx-px*w2,hy-py*w2),(nx-px*w1,ny-py*w1)]
    o+='<path class="f" d="M'+' L'.join(f'{x:.1f} {y:.1f}' for x,y in pts)+'Z"/>'
    if shirt=='s':  # смугаста форма
        for t in (0.35,0.65):
            ax=nx+(hx-nx)*t; ay=ny+(hy-ny)*t; ww=w1+(w2-w1)*t
            o+=f'<path d="M{ax+px*ww:.1f} {ay+py*ww:.1f}L{ax-px*ww:.1f} {ay-py*ww:.1f}" stroke-width="{1.4*s:.1f}"/>'
    if shirt=='n':  # номер
        o+=f'<path d="M{(nx+hx)/2:.1f} {(ny+hy)/2-3*s:.1f}v{6*s:.1f}" stroke-width="{1.4*s:.1f}"/>'
    if g=='g' and not shorts:
        o+=f'<path class="f" d="M{hx+px*w2:.1f} {hy+py*w2:.1f}L{hx+px*(w2+4*s)+(hx-nx)*.35:.1f} {hy+py*(w2+4*s)+(hy-ny)*.35:.1f}L{hx-px*(w2+4*s)+(hx-nx)*.35:.1f} {hy-py*(w2+4*s)+(hy-ny)*.35:.1f}L{hx-px*w2:.1f} {hy-py*w2:.1f}Z"/>'
    else:
        o+=f'<path d="M{hx+px*w2:.1f} {hy+py*w2:.1f}L{hx-px*w2:.1f} {hy-py*w2:.1f}" stroke-width="{1.4*s:.1f}"/>'
    o+=f'<path d="M{nx-3*s:.1f} {ny+.5:.1f}q{3*s:.1f} {3*s:.1f} {6*s:.1f} 0" stroke-width="{1.4*s:.1f}"/>'
    # передні кінцівки
    o+=tube([j['hip'],j['kR'],j['fR']],low,liw)+shoe(*j['fR'],1 if j['fR'][0]>=j['kR'][0] else -1)
    hd=(nx-(hx-nx)*0.52,ny-(hy-ny)*0.52-3*s)
    o+=tube([j['neck'],j['eR'],j['hR']],ow,iw)+hand(*j['hR'],2.6*s)
    o+=head(round(hd[0],1),round(hd[1],1),g,s*0.95,flip)
    if hat=='cap': o+=f'<path class="f" d="M{hd[0]-10*s:.1f} {hd[1]-5*s:.1f}q{10*s:.1f} -{12*s:.1f} {20*s:.1f} 0z"/><path d="M{hd[0]+8*s:.1f} {hd[1]-5*s:.1f}h{8*s:.1f}" stroke-width="{2.4*s:.1f}"/>'
    return o
def grass(x,y): return ln(f'M{x} {y}l2-5 2 5M{x+5} {y}l2-7 2 7M{x+11} {y}l1.5-4 1.5 4',1.6)
DEFS=('<defs><g id="hb"><circle class="f" r="11"/><path class="f" d="M-11 -1Q-12 -14 0 -13Q12 -14 11 -1Q8 -8 2 -7Q-3 -10 -6 -5Q-9 -7 -11 -1Z"/>'
 '<circle class="d" cx="-4" cy="1" r="1.5"/><circle class="d" cx="4" cy="1" r="1.5"/><path d="M-4 5Q0 9 4 5" stroke-width="1.5"/></g>'
 '<g id="hg"><circle class="f" cx="-12" cy="-3" r="4.5"/><circle class="f" cx="12" cy="-3" r="4.5"/><circle class="f" r="11"/>'
 '<path class="f" d="M-11 -1Q-11 -14 0 -12Q11 -14 11 -1Q6 -7 0 -5Q-6 -7 -11 -1Z"/><circle class="d" cx="-4" cy="1" r="1.5"/><circle class="d" cx="4" cy="1" r="1.5"/><path d="M-4 5Q0 9 4 5" stroke-width="1.5"/></g>'
 '<g id="ha"><circle class="f" r="11"/><path class="f" d="M-11 -2Q-10 -14 0 -13Q10 -14 11 -2Q9 -6 6 -7Q0 -5 -6 -7Q-9 -6 -11 -2Z"/>'
 '<circle class="d" cx="-4" cy="1" r="1.5"/><circle class="d" cx="4" cy="1" r="1.5"/><path d="M-4 5.5Q0 8 4 5.5" stroke-width="1.5"/></g></defs>')
