import getpass
import xmlrpc.client

def main():
    # 1) Paramètres de connexion
    url = input("URL Odoo (ex: http://localhost:8069) : ").strip()
    db = input("Nom de la base (db) : ").strip()
    login = input("Login : ").strip()

    # mot de passe OU clé API (dans les deux cas c'est juste une 'string' pour Odoo)
    password = getpass.getpass("Mot de passe ou clé API : ")

    # 2) Endpoints XML-RPC
    common = xmlrpc.client.ServerProxy(f"{url}/xmlrpc/2/common")
    uid = common.authenticate(db, login, password, {})
    if not uid:
        print("Echec d'authentification. Vérifie db/login/password.")
        return

    print(f"Connecté. uid={uid}")

    models = xmlrpc.client.ServerProxy(f"{url}/xmlrpc/2/object")

    # 3) Boucle de recherche
    while True:
        author_name = input("\nNom de l'auteur à chercher (vide pour quitter) : ").strip()
        if not author_name:
            print("Fin.")
            break

        # Domaine : feedback dont l'auteur (res.users) a un name qui contient author_name
        domain = [('author_id.name', 'ilike', author_name)]

        fields = ['id', 'author_id', 'application_id', 'state', 'description', 'questions_count', 'average_score']
        results = models.execute_kw(
            db, uid, password,
            'hr.feedback', 'search_read',
            [domain],
            {'fields': fields, 'limit': 50}
        )

        if not results:
            print("Aucun feedback trouvé.")
            continue

        for fb in results:
            author = fb['author_id'][1] if fb.get('author_id') else None
            app = fb['application_id'][1] if fb.get('application_id') else None
            print("-" * 60)
            print(f"ID: {fb['id']}")
            print(f"Auteur: {author}")
            print(f"Candidature: {app}")
            print(f"Etat: {fb['state']}")
            print(f"Nb questions: {fb['questions_count']}, Moyenne: {fb['average_score']}")
            print(f"Description: {fb['description']}")

if __name__ == "__main__":
    main()
