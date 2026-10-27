"""Figura 7.2 - Restricción de Burt del ego (tú) en cuatro redes SINTÉTICAS de 6 personas, calculada como vm_red()
(ego unido a todas; igraph::constraint). Se implementa la fórmula a mano y se compara con networkx.
Uso: python fig12_burt_puente.py [salida.png]"""
import sys, itertools, math
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

def restriccion(adj, i):
    """C_i = Σ_j (p_ij + Σ_q p_iq·p_qj)², con p_ij = a_ij / Σ_k a_ik (red no dirigida y sin pesos)."""
    n = len(adj); s = adj.sum(1)
    p = np.divide(adj, s[:, None], out=np.zeros_like(adj, float), where=s[:, None] > 0)
    c = 0.0
    for j in range(n):
        if j == i or adj[i, j] == 0: continue
        ind = sum(p[i, q] * p[q, j] for q in range(n) if q not in (i, j))
        c += (p[i, j] + ind) ** 2
    return c

ALT = ["a1", "a2", "a3", "b1", "b2", "b3"]
grupoA = [(0, 1), (0, 2), (1, 2)]; grupoB = [(3, 4), (3, 5), (4, 5)]
escenarios = [
    ("Nadie se conoce", []),
    ("Dos grupos sin puente", grupoA + grupoB),
    ("Dos grupos con un puente\n(a3 conoce a b1)", grupoA + grupoB + [(2, 3)]),
    ("Todos se conocen", list(itertools.combinations(range(6), 2))),
]
ang = [math.pi * (0.62 + 0.18 * k) for k in range(3)] + [math.pi * (-0.38 + 0.18 * k) for k in range(3)]
xy = np.array([[math.cos(a), math.sin(a)] for a in ang])

fig, ejes = plt.subplots(1, 4, figsize=(12.5, 4.0), dpi=160)
resultados = []
for ax, (titulo, aristas) in zip(ejes, escenarios):
    n = 7; adj = np.zeros((n, n))
    for a, b in aristas: adj[a, b] = adj[b, a] = 1
    for k in range(6): adj[6, k] = adj[k, 6] = 1          # ego («yo») unido a todas, como en vm_red()
    C = restriccion(adj, 6)
    dens = len(aristas) / (6 * 5 / 2)                       # densidad sin el ego (edge_density(g) y la de la app)
    try:
        import networkx as nx
        G = nx.from_numpy_array(adj); Cnx = nx.constraint(G, [6])[6]
        assert abs(Cnx - C) < 1e-12, (Cnx, C)
    except ImportError:
        pass
    resultados.append((titulo.replace("\n", " "), C, dens))
    ax.set_aspect("equal"); ax.axis("off"); ax.set_xlim(-1.35, 1.35); ax.set_ylim(-1.45, 1.35)
    for k in range(6): ax.plot([0, xy[k, 0]], [0, xy[k, 1]], color="#d1d5db", lw=1, zorder=1)
    for a, b in aristas:
        puente = (a, b) == (2, 3) and "puente" in titulo
        ax.plot(*zip(xy[a], xy[b]), color="#bc6c25" if puente else "#2a6f97", lw=2.6 if puente else 1.8, zorder=2)
    ax.scatter(xy[:3, 0], xy[:3, 1], s=230, color="#6a994e", zorder=3); ax.scatter(xy[3:, 0], xy[3:, 1], s=230, color="#e09f3e", zorder=3)
    for k, t in enumerate(ALT): ax.text(*xy[k], t, ha="center", va="center", fontsize=7, color="white", zorder=4)
    ax.scatter([0], [0], s=260, color="#1f2937", zorder=3); ax.text(0, 0, "tú", ha="center", va="center", fontsize=7.5, color="white", zorder=4)
    ax.set_title(titulo, fontsize=9, color="#1f2937")
    ax.text(0, -1.3, f"restricción C = {C:.3f}\ndensidad = {dens:.0%}", ha="center", va="center", fontsize=8.5, color="#1f2937")

fig.text(0.01, 0.02, "Líneas grises: tú con cada persona (vm_red() las agrega siempre). Azul: se conocen entre sí. Naranja: el puente. "
         "Menor C = tus contactos se conocen menos entre sí (más «huecos estructurales»).", fontsize=7.8, color="#374151")
fig.suptitle("Figura 7.2  Restricción de Burt del ego en cuatro redes de 6 personas (datos sintéticos)", x=0.01, ha="left", fontsize=11, weight="bold", color="#1f2937")
fig.tight_layout(rect=(0, 0.05, 1, .93))
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig12_burt_puente.png")
for t, C, d in resultados: print(f"{t:40s} C={C:.4f} densidad={d:.3f}")
