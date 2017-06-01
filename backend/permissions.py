from rest_framework import permissions


class UserPermission(permissions.BasePermission):
    USER_PROTECTED_ACTIONS = ['update', 'get_user']
    USER_SAFE_ACTIONS = ['create', 'signin']

    def has_permission(self, request, view):
        if request.user and request.user.is_authenticated():
            if request.user.is_staff:
                return True
            else:
                if view.action in self.USER_PROTECTED_ACTIONS:
                    return True
        else:
            if view.action in self.USER_SAFE_ACTIONS:
                return True
        return False

    def has_object_permission(self, request, view, obj):
        # Read permissions are allowed to any request,
        # so we'll always allow GET, HEAD or OPTIONS requests.
        if request.method in permissions.SAFE_METHODS:
            return True

        # Check owner.
        return obj == request.user


class CategoryPermission(permissions.BasePermission):

    def has_permission(self, request, view):
        user = request.user
        if user and user.is_authenticated():
            if user.is_staff or user.profile.role == 2:
                return True
        return False


class ServicePermission(permissions.BasePermission):

    def has_permission(self, request, view):
        user = request.user
        if user and user.is_authenticated():
            if user.is_staff or user.profile.role == 1:
                return True
        return False
