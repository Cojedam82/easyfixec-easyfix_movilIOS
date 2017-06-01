from rest_framework import viewsets
from rest_framework.authentication import TokenAuthentication, SessionAuthentication
from rest_framework.decorators import detail_route, list_route

from .models import *
from .permissions import *
from .serializers import *


class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer
    authentication_classes = (SessionAuthentication, TokenAuthentication)
    permission_classes = (UserPermission,)

    def create(self, request):
        response = {
            'error':True,
            'msg':'Parámetros erroneos',
            'data':{}
        }

        # Get data
        data = request.data

        try:
            # Get user data
            first_name = data['first_name']
            last_name = data['last_name']
            password = data['password']
            email = data['email']
            username = email

            # Get profile data
            phone = data['profile']['phone']

            # Check for email
            user = User.objects.filter(email=email)
            if user:
                response['msg'] = 'El correo ya se encuentra registrado!'
                return Response(response)

            # Create user
            user = User(username=username, email=email, first_name=first_name, last_name=last_name)
            user.set_password(password)
            user.save()

            # Create profile
            user.profile.phone = phone
            user.profile.save()

            # Serializer User
            serializer = UserSerializer(user)
            user = serializer.data

            response['error'] = False
            response['msg'] = 'Usuario registrado correctamente'
            response['data'] = user
        except Exception as e:
            response['msg'] = str(e)

        return Response(response)

    @list_route(url_path='login', methods=['post'])
    def signin(self, request):
        response = {
            'error':True,
            'msg':'Parámetros erroneos',
            'data':{}
        }

        email = request.POST.get('email', None)
        password = request.POST.get('password', None)

        if email and password:
            user = User.objects.filter(email=email).first()

            if user and user.check_password(password):

                # Serializer User
                serializer = UserSerializer(user)
                data = serializer.data

                response['error'] = False
                response['msg'] = 'Inicio de sesión exitoso'
                response['data'] = data
            else:
                response['msg'] = 'Usuario o Contraseña incorrectos'

        print(response)
        return Response(response)

    @list_route(url_path='user', methods=['get'])
    def get_user(self, request):
        user = self.request.user
        serializer = UserSerializer(user)
        return Response(serializer.data)


class CategoryViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Category.objects.filter(is_active=True)
    serializer_class = CategorySerializer
    authentication_classes = (SessionAuthentication, TokenAuthentication)
    permission_classes = (CategoryPermission,)


class ServiceViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Service.objects.filter(is_active=True)
    serializer_class = ServiceSerializer
    authentication_classes = (SessionAuthentication, TokenAuthentication)
    permission_classes = (ServicePermission,)
