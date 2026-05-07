
Ce document décrit la méthode à suivre pour que toute l'équipe puisse coder en parallèle sans casser `main` ni `prod`.

## Objectif
- Chaque développeur créé une **branche par User Story (US)**.
- Quand la fonctionnalité est prête, on ouvre une **PR vers `main`**.
- `main` et `prod` sont protégées et ne doivent pas recevoir de commits directs.

## Règles de base
1. **Toujours partir de `dev`**
   ```bash
   git switch main
   git pull origin main
   ```
2. **Créer une branche nommée clairement**
    - Format recommandé : `feature/US<id>`
    - Exemples : `feature/US1`, `bugfix/US1`
3. **Commit fréquents et messages clairs**
    - **Avant** de faire votre **premier** commit, exécuter cette commande à la racine du projet
    
    ```shell 
        git config core.hooksPath .githooks
    ```
    
    - Pensez à lier votre commit à une US donc en rajoutant le # de l'US dans le     commit, ainsi que de respecter la convention.

    - git commit -m"<type>(<optional scope>): <description>" \
                 -m"<optional body>" \
                 -m"<optional footer>"
    - Voici la liste des types autorisés :
        - build
        - chore
        - ci
        - docs
        - feat
        - fix
        - perf
        - refactor
        - revert
        - style
        - test

    - Exemples valide: 
        - `fix(api): fix wrong calculation of request body checksum`
        - `ci(hooks): commit name hook added`

    - Exemples invalide: 
        - `fix`
        - `foo:invalid commit message`

4. **Push de la branche**
   ```bash
   git push origin feature/us1
   ```



## Quand ouvrir une PR (et vers quelle branche)
1. **PR vers `dev`**
    - Ouvrir une PR depuis ta branche feature → `dev` quand la story est terminée et testée localement.
2. **PR vers `main`**
    - Ouvrir une PR depuis `dev` → `main` quand la branche est assez stable pour correspondre a une release/tag.
2. **Contenu de la PR**
    - Lier l'issue (ex : `Closes #123` ou `Fixes US1`)

## Comment faire une release
- Lors d'une pull request vers `dev` ajouter dans le titre de la PR **`[major]`, `[minor]`** ou **`[patch]`**

 
## Déploiement en prod
- `main` est la branche d'intégration.
- Quand plusieurs features sont validées et testées, on effectue **PR `main` → `prod`** pour déployer.


## Exemples de commandes utiles
```bash
# créer une branche feature
git switch main
git pull origin main
git switch -c feature/US1-login-page

# push
git push origin feature/US1-login-page
```


## Stratégie et structure du repo
Le repo fonctionne de manière similaire à un trunk base, ou dev est le trunk et chaque dev travaille dans sa branche puis pull request dans dev.
dev n'est pas forcément stable.
main est la branche de release, elle est stable et le code y est envoyé lors de release (release-it) et doit être review.

```texte

        Feature 1             Feature 2              Release              Feature 4
        _________             _________              _______              _________
            |                     |                     |                     |
dev         |                     |                     |                     |
--------------------------------------------------------◻------------------------------------------>
                                                        |
                                                        | Changelogs
main                                                    |
--------------------------------------------------------------------------------------------------->
```

## Final
Merci de suivre ces règles pour garder le repo lisible et les déploiements sûrs. Si l'équipe souhaite modifier la convention (ex : préfixe, stratégie de merge, nombre d'approbations), on mettra à jour ce fichier.
